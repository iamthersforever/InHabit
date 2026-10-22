import mongoose from 'mongoose';

// Unique userId makes this a one-to-one profile collection: one profile per account.
const userProfileSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, unique: true },
  block: { type: String, trim: true, default: '' },
  room: { type: String, trim: true, default: '' },
  wing: { type: String, trim: true, default: '' }
}, { timestamps: true, versionKey: false });

export default mongoose.model('UserProfile', userProfileSchema);
