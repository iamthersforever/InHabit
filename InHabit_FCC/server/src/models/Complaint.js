import mongoose from 'mongoose';
import { CATEGORIES, COMPLAINT_STATUSES } from '../utils/validation.js';

const statusEventSchema = new mongoose.Schema({
  status: { type: String, enum: COMPLAINT_STATUSES, required: true },
  note: { type: String, trim: true, maxlength: 300, default: '' },
  changedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User', default: null },
  changedAt: { type: Date, default: Date.now }
}, { _id: false });

const complaintSchema = new mongoose.Schema({
  ticketNumber: { type: String, unique: true, required: true, index: true },
  title: { type: String, required: true, trim: true, minlength: 4, maxlength: 120 },
  description: { type: String, required: true, trim: true, minlength: 10, maxlength: 2000 },
  category: { type: String, enum: CATEGORIES, required: true, index: true },
  location: { type: String, required: true, trim: true, maxlength: 120 },
  status: { type: String, enum: COMPLAINT_STATUSES, default: 'Open', index: true },
  attachmentUrl: { type: String, trim: true, default: '' },
  createdBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  assignedTo: { type: mongoose.Schema.Types.ObjectId, ref: 'User', default: null },
  voteCount: { type: Number, min: 0, default: 0 },
  concerning: { type: Boolean, default: false, index: true },
  isAnonymous: { type: Boolean, default: false },
  statusHistory: { type: [statusEventSchema], default: [] }
}, { timestamps: true, versionKey: false });

complaintSchema.index({ status: 1, category: 1, createdAt: -1 });
complaintSchema.index({ title: 'text', description: 'text', location: 'text' });

export default mongoose.model('Complaint', complaintSchema);
