import mongoose from 'mongoose';

// A Vote document is the bridge between User and Complaint (many-to-many relationship).
const voteSchema = new mongoose.Schema({
  complaintId: { type: mongoose.Schema.Types.ObjectId, ref: 'Complaint', required: true },
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  createdAt: { type: Date, default: Date.now }
}, { versionKey: false });

voteSchema.index({ complaintId: 1, userId: 1 }, { unique: true });
voteSchema.index({ userId: 1, createdAt: -1 });

export default mongoose.model('Vote', voteSchema);
