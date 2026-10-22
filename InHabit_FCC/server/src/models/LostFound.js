import mongoose from 'mongoose';

const lostFoundSchema = new mongoose.Schema({
  itemName: { type: String, required: true, trim: true, minlength: 2, maxlength: 100 },
  description: { type: String, required: true, trim: true, maxlength: 1000 },
  location: { type: String, required: true, trim: true, maxlength: 120 },
  itemType: { type: String, enum: ['Lost', 'Found'], required: true, index: true },
  status: { type: String, enum: ['Open', 'Found', 'Claimed', 'Returned'], default: 'Open', index: true },
  imageUrl: { type: String, trim: true, default: '' },
  reportedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  claimedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User', default: null },
  contactNote: { type: String, trim: true, maxlength: 160, default: '' }
}, { timestamps: true, versionKey: false });

lostFoundSchema.index({ status: 1, itemType: 1, createdAt: -1 });
lostFoundSchema.index({ itemName: 'text', description: 'text', location: 'text' });

export default mongoose.model('LostFound', lostFoundSchema);
