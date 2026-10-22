import { Router } from 'express';
import multer from 'multer';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import Complaint from '../models/Complaint.js';
import Vote from '../models/Vote.js';
import User from '../models/User.js';
import { authenticate, requireRole } from '../middleware/auth.js';
import { CATEGORIES, COMPLAINT_STATUSES, cleanText, escapeRegex, makeTicketNumber } from '../utils/validation.js';
import { publishAudit } from '../services/audit.js';

const router = Router();
router.use(authenticate);

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const uploadDirectory = path.resolve(__dirname, '../../uploads');
const storage = multer.diskStorage({
  destination: (_req, _file, callback) => callback(null, uploadDirectory),
  filename: (_req, file, callback) => {
    const ext = path.extname(file.originalname).toLowerCase();
    callback(null, `${Date.now()}-${Math.random().toString(36).slice(2, 8)}${ext}`);
  }
});
const upload = multer({
  storage,
  limits: { fileSize: 5 * 1024 * 1024, files: 1 },
  fileFilter: (_req, file, callback) => {
    const allowed = ['image/jpeg', 'image/png', 'image/webp'];
    if (!allowed.includes(file.mimetype)) return callback(new Error('Only JPG, PNG, and WEBP image files are accepted.'));
    callback(null, true);
  }
});

router.post('/upload', requireRole('student', 'admin', 'technician'), upload.single('image'), (req, res) => {
  if (!req.file) return res.status(400).json({ message: 'Choose an image to upload.' });
  const url = `/uploads/${req.file.filename}`;
  publishAudit('attachment.uploaded', { userId: req.user._id.toString(), filename: req.file.filename });
  return res.status(201).json({ url, filename: req.file.filename, size: req.file.size });
});

router.get('/', async (req, res, next) => {
  try {
    const { q = '', status = '', category = '', mine = 'false' } = req.query;
    const filter = {};
    if (status && COMPLAINT_STATUSES.includes(status)) filter.status = status;
    if (category && CATEGORIES.includes(category)) {
      if (category === 'Mess' || category === 'Mess/Food') {
        filter.category = { $in: ['Mess', 'Mess/Food'] };
      } else {
        filter.category = category;
      }
    }
    if (mine === 'true') filter.createdBy = req.user._id;
    if (req.user.role === 'technician') filter.assignedTo = req.user._id;
    const search = cleanText(q, 80);
    if (search) {
      const pattern = new RegExp(escapeRegex(search), 'i');
      const textFilter = [{ title: pattern }, { description: pattern }, { location: pattern }, { ticketNumber: pattern }];
      if (filter.$or) filter.$and = [{ $or: filter.$or }, { $or: textFilter }];
      else filter.$or = textFilter;
    }
    const complaints = await Complaint.find(filter)
      .populate('createdBy', 'name role email prn profile')
      .populate('assignedTo', 'name email trade department role')
      .sort({ concerning: -1, createdAt: -1 }).limit(100).lean();
    const ids = complaints.map((item) => item._id);
    const myVotes = ids.length ? await Vote.find({ complaintId: { $in: ids }, userId: req.user._id }).select('complaintId').lean() : [];
    const votedIds = new Set(myVotes.map((vote) => vote.complaintId.toString()));
    return res.json({ complaints: complaints.map((item) => {
      const row = { ...item, id: item._id.toString(), currentUserVoted: votedIds.has(item._id.toString()) };
      const owns = row.createdBy?._id?.toString() === req.user._id.toString();
      if (row.isAnonymous && !owns && req.user.role !== 'admin') row.createdBy = { name: 'Anonymous resident' };
      return row;
    }) });
  } catch (error) { return next(error); }
});

router.post('/', requireRole('student'), async (req, res, next) => {
  try {
    const title = cleanText(req.body.title, 120);
    const description = cleanText(req.body.description, 2000);
    const category = cleanText(req.body.category, 50);
    const location = cleanText(req.body.location, 120);
    const attachmentUrl = cleanText(req.body.attachmentUrl, 300);
    if (title.length < 4 || description.length < 10 || !CATEGORIES.includes(category) || location.length < 2) {
      return res.status(400).json({ message: 'Add a descriptive title, a description of at least 10 characters, a category, and a location.' });
    }
    const complaint = await Complaint.create({
      ticketNumber: makeTicketNumber(), title, description, category, location, attachmentUrl,
      createdBy: req.user._id, isAnonymous: Boolean(req.body.isAnonymous),
      statusHistory: [{ status: 'Open', note: 'Grievance submitted', changedBy: req.user._id }]
    });
    publishAudit('complaint.created', { complaintId: complaint._id.toString(), ticketNumber: complaint.ticketNumber, userId: req.user._id.toString() });
    const populated = await Complaint.findById(complaint._id).populate('createdBy', 'name role').lean();
    return res.status(201).json({ complaint: { ...populated, id: populated._id.toString(), currentUserVoted: false } });
  } catch (error) { return next(error); }
});

router.post('/:id/vote', requireRole('student'), async (req, res, next) => {
  try {
    const complaint = await Complaint.findById(req.params.id);
    if (!complaint) return res.status(404).json({ message: 'Grievance not found.' });
    if (complaint.createdBy.toString() === req.user._id.toString()) return res.status(400).json({ message: 'You cannot vote on your own grievance.' });
    if (['Resolved', 'Closed'].includes(complaint.status)) return res.status(400).json({ message: 'Voting is closed for resolved grievances.' });
    try {
      await Vote.create({ complaintId: complaint._id, userId: req.user._id });
    } catch (error) {
      if (error?.code === 11000) return res.status(409).json({ message: 'You have already supported this grievance.' });
      throw error;
    }
    const updated = await Complaint.findByIdAndUpdate(complaint._id, { $inc: { voteCount: 1 } }, { returnDocument: 'after' });
    if (updated.voteCount >= 30 && !updated.concerning) {
      updated.concerning = true;
      await updated.save();
      publishAudit('complaint.escalated', { complaintId: updated._id.toString(), ticketNumber: updated.ticketNumber, voteCount: updated.voteCount });
    }
    publishAudit('complaint.voted', { complaintId: updated._id.toString(), userId: req.user._id.toString(), voteCount: updated.voteCount });
    return res.json({ message: updated.concerning ? 'Vote recorded. This grievance is flagged as concerning.' : 'Vote recorded.', voteCount: updated.voteCount, concerning: updated.concerning });
  } catch (error) { return next(error); }
});

router.patch('/:id/status', requireRole('admin', 'technician'), async (req, res, next) => {
  try {
    const status = cleanText(req.body.status, 40);
    const note = cleanText(req.body.note || `Status changed to ${status}`, 300);
    if (!COMPLAINT_STATUSES.includes(status)) return res.status(400).json({ message: 'Choose a valid status.' });
    const complaint = await Complaint.findById(req.params.id);
    if (!complaint) return res.status(404).json({ message: 'Grievance not found.' });
    if (req.user.role === 'technician' && complaint.assignedTo?.toString() !== req.user._id.toString()) {
      return res.status(403).json({ message: 'Technicians can update only grievances assigned to them.' });
    }
    const oldStatus = complaint.status;
    complaint.status = status;
    complaint.statusHistory.push({ status, note, changedBy: req.user._id, changedAt: new Date() });
    await complaint.save();
    publishAudit('complaint.status_changed', { complaintId: complaint._id.toString(), userId: req.user._id.toString(), oldStatus, status });
    return res.json({ message: 'Status updated.', complaint });
  } catch (error) { return next(error); }
});

router.patch('/:id/assign', requireRole('admin'), async (req, res, next) => {
  try {
    const technician = await User.findOne({ _id: req.body.technicianId, role: 'technician', isActive: true });
    if (!technician) return res.status(400).json({ message: 'Choose an active technician account.' });
    const complaint = await Complaint.findById(req.params.id);
    if (!complaint) return res.status(404).json({ message: 'Grievance not found.' });
    complaint.assignedTo = technician._id;
    if (complaint.status === 'Open') complaint.status = 'Under Review';
    complaint.statusHistory.push({ status: complaint.status, note: `Assigned to ${technician.name}`, changedBy: req.user._id });
    await complaint.save();
    publishAudit('complaint.assigned', { complaintId: complaint._id.toString(), technicianId: technician._id.toString(), assignedBy: req.user._id.toString() });
    return res.json({ message: `Assigned to ${technician.name}.`, complaint });
  } catch (error) { return next(error); }
});

router.delete('/:id', requireRole('admin'), async (req, res, next) => {
  try {
    const complaint = await Complaint.findByIdAndDelete(req.params.id);
    if (!complaint) return res.status(404).json({ message: 'Grievance not found.' });
    await Vote.deleteMany({ complaintId: complaint._id });
    publishAudit('complaint.deleted', { complaintId: complaint._id.toString(), userId: req.user._id.toString() });
    return res.json({ message: 'Grievance deleted.' });
  } catch (error) { return next(error); }
});

export default router;
