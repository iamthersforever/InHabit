import { Router } from 'express';
import mongoose from 'mongoose';
import Complaint from '../models/Complaint.js';
import LostFound from '../models/LostFound.js';
import { authenticate, requireRole } from '../middleware/auth.js';

const router = Router();
router.use(authenticate);

router.get('/stats', async (req, res, next) => {
  try {
    const [totalsRows, categoryBreakdown, statusBreakdown, recentComplaints, openLostFound] = await Promise.all([
      Complaint.aggregate([{ $group: {
        _id: null,
        total: { $sum: 1 },
        active: { $sum: { $cond: [{ $in: ['$status', ['Open', 'Under Review', 'In Progress']] }, 1, 0] } },
        concerning: { $sum: { $cond: ['$concerning', 1, 0] } },
        resolved: { $sum: { $cond: [{ $in: ['$status', ['Resolved', 'Closed']] }, 1, 0] } }
      } }]),
      Complaint.aggregate([{ $group: { _id: '$category', count: { $sum: 1 } } }, { $sort: { count: -1, _id: 1 } }]),
      Complaint.aggregate([{ $group: { _id: '$status', count: { $sum: 1 } } }, { $sort: { count: -1, _id: 1 } }]),
      Complaint.find().populate('createdBy', 'name').sort({ createdAt: -1 }).limit(5).lean(),
      LostFound.countDocuments({ status: 'Open' })
    ]);
    const raw = totalsRows[0] || { total: 0, active: 0, concerning: 0, resolved: 0 };
    return res.json({
      totals: { ...raw, resolutionRate: raw.total ? Math.round((raw.resolved / raw.total) * 100) : 0, openLostFound },
      categoryBreakdown: categoryBreakdown.map((row) => ({ category: row._id || 'Uncategorised', count: row.count })),
      statusBreakdown: statusBreakdown.map((row) => ({ status: row._id, count: row.count })),
      recentComplaints: recentComplaints.map((item) => {
        const row = { ...item, id: item._id.toString() };
        const owns = row.createdBy?._id?.toString() === req.user._id.toString();
        if (row.isAnonymous && !owns && req.user.role !== 'admin') row.createdBy = { name: 'Anonymous resident' };
        return row;
      }),
      database: mongoose.connection.readyState === 1 ? 'connected' : 'disconnected'
    });
  } catch (error) { return next(error); }
});

router.get('/technicians', requireRole('admin'), async (_req, res, next) => {
  try {
    const User = (await import('../models/User.js')).default;
    const technicians = await User.find({ role: 'technician', isActive: true }).select('name email trade department profile').sort({ name: 1 }).lean();
    return res.json({ technicians: technicians.map((tech) => ({ ...tech, id: tech._id.toString() })) });
  } catch (error) { return next(error); }
});

router.get('/roommates', async (req, res, next) => {
  try {
    const prn = req.user.prn || '';
    let roommates = [];
    // Room occupants derived from InHabitDBNew.sql STUDENT table (BlockName='Alpha', Wing='101'/'102')
    if (prn === '257122235' || req.user.email?.includes('raghav.sarda')) {
      // Alpha Wing 101: USR_257122223 (Yatharth Akshay Shah), USR_257122299 (Parth Shrikant Kulkarni)
      roommates = [
        { name: 'Yatharth Akshay Shah', prn: '257122223', room: 'Alpha-101', branch: 'B.Tech CSE - C', email: 'yatharth.shah.btech25@college.edu.in', phone: '+91 98761 22223' },
        { name: 'Parth Shrikant Kulkarni', prn: '257122299', room: 'Alpha-101', branch: 'B.Tech CSE - C', email: 'parth.kulkarni.btech25@college.edu.in', phone: '+91 98761 22299' }
      ];
    } else if (prn === '257122247' || req.user.email?.includes('aniruddha.singh')) {
      // Alpha Wing 102: USR_257122218 (Vishrut Sandeep Chinta)
      roommates = [
        { name: 'Vishrut Sandeep Chinta', prn: '257122218', room: 'Alpha-102', branch: 'B.Tech CSE - C', email: 'vishrut.chinta.btech25@college.edu.in', phone: '+91 98761 22218' }
      ];
    }
    return res.json({ roommates });
  } catch (error) { return next(error); }
});

router.get('/residents', requireRole('admin'), async (_req, res, next) => {
  try {
    const User = (await import('../models/User.js')).default;
    const students = await User.find({ role: 'student' }).populate('profile').select('name email prn profile phone createdAt').lean();
    return res.json({ residents: students.map(s => ({ ...s, id: s._id.toString() })) });
  } catch (error) { return next(error); }
});

router.get('/staff', requireRole('admin'), async (_req, res, next) => {
  try {
    const User = (await import('../models/User.js')).default;
    const staff = await User.find({ role: { $in: ['admin', 'technician'] } }).select('name email role trade department staffId phone createdAt').sort({ role: 1, name: 1 }).lean();
    return res.json({ staff: staff.map(s => ({ ...s, id: s._id.toString() })) });
  } catch (error) { return next(error); }
});

export default router;
