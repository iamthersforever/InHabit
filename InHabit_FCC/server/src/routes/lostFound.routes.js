import { Router } from 'express';
import LostFound from '../models/LostFound.js';
import { authenticate, requireRole } from '../middleware/auth.js';
import { cleanText, escapeRegex } from '../utils/validation.js';
import { publishAudit } from '../services/audit.js';

const router = Router();
router.use(authenticate);

router.get('/', async (req, res, next) => {
  try {
    const filter = {};
    if (['Lost', 'Found'].includes(req.query.type)) filter.itemType = req.query.type;
    if (['Open', 'Found', 'Claimed', 'Returned'].includes(req.query.status)) filter.status = req.query.status;
    if (req.query.mine === 'true') filter.reportedBy = req.user._id;
    const q = cleanText(req.query.q, 80);
    if (q) {
      const re = new RegExp(escapeRegex(q), 'i');
      filter.$or = [{ itemName: re }, { description: re }, { location: re }];
    }
    const items = await LostFound.find(filter).populate('reportedBy', 'name').populate('claimedBy', 'name').sort({ createdAt: -1 }).limit(100).lean();
    return res.json({ items: items.map((item) => ({ ...item, id: item._id.toString() })) });
  } catch (error) { return next(error); }
});

router.post('/', requireRole('student'), async (req, res, next) => {
  try {
    const itemName = cleanText(req.body.itemName, 100);
    const description = cleanText(req.body.description, 1000);
    const location = cleanText(req.body.location, 120);
    const itemType = cleanText(req.body.itemType, 10);
    if (itemName.length < 2 || !description || location.length < 2 || !['Lost', 'Found'].includes(itemType)) {
      return res.status(400).json({ message: 'Item name, description, location, and Lost/Found type are required.' });
    }
    const item = await LostFound.create({ itemName, description, location, itemType, imageUrl: cleanText(req.body.imageUrl, 300), contactNote: cleanText(req.body.contactNote, 160), reportedBy: req.user._id });
    publishAudit('lostfound.created', { itemId: item._id.toString(), userId: req.user._id.toString(), itemType });
    const populated = await LostFound.findById(item._id).populate('reportedBy', 'name email').lean();
    return res.status(201).json({ item: { ...populated, id: populated._id.toString() } });
  } catch (error) { return next(error); }
});

router.patch('/:id/claim', async (req, res, next) => {
  try {
    const item = await LostFound.findById(req.params.id);
    if (!item) return res.status(404).json({ message: 'Lost-and-found post not found.' });
    if (item.reportedBy.toString() === req.user._id.toString()) return res.status(400).json({ message: 'You cannot claim your own post.' });
    if (item.status !== 'Open') return res.status(409).json({ message: 'This post is no longer open.' });
    item.status = item.itemType === 'Lost' ? 'Found' : 'Claimed';
    item.claimedBy = req.user._id;
    await item.save();
    publishAudit('lostfound.claimed', { itemId: item._id.toString(), userId: req.user._id.toString(), status: item.status });
    return res.json({ message: item.itemType === 'Lost' ? 'Marked as found. The reporter can now follow up.' : 'Claim request recorded.', item });
  } catch (error) { return next(error); }
});

router.patch('/:id/returned', async (req, res, next) => {
  try {
    const item = await LostFound.findById(req.params.id);
    if (!item) return res.status(404).json({ message: 'Lost-and-found post not found.' });
    const isReporter = item.reportedBy.toString() === req.user._id.toString();
    const isAdmin = req.user.role === 'admin';
    if (!isReporter && !isAdmin) return res.status(403).json({ message: 'Only the reporter or an admin can close this post.' });
    item.status = 'Returned';
    await item.save();
    publishAudit('lostfound.returned', { itemId: item._id.toString(), userId: req.user._id.toString() });
    return res.json({ message: 'Post marked as returned.', item });
  } catch (error) { return next(error); }
});

router.delete('/:id', async (req, res, next) => {
  try {
    const item = await LostFound.findById(req.params.id);
    if (!item) return res.status(404).json({ message: 'Lost-and-found post not found.' });
    if (item.reportedBy.toString() !== req.user._id.toString() && req.user.role !== 'admin') {
      return res.status(403).json({ message: 'You can delete only your own post.' });
    }
    await item.deleteOne();
    publishAudit('lostfound.deleted', { itemId: item._id.toString(), userId: req.user._id.toString() });
    return res.json({ message: 'Post deleted.' });
  } catch (error) { return next(error); }
});

export default router;
