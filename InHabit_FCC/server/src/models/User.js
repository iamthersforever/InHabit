import mongoose from 'mongoose';

const userSchema = new mongoose.Schema({
  name: { type: String, required: true, trim: true, minlength: 2, maxlength: 80 },
  email: { type: String, required: true, unique: true, lowercase: true, trim: true },
  passwordHash: { type: String, required: true, select: false },
  role: { type: String, enum: ['student', 'admin', 'technician'], default: 'student', index: true },
  prn: { type: String, trim: true, sparse: true, unique: true },
  staffId: { type: String, trim: true, sparse: true },
  // Reference to a separate unique UserProfile document demonstrates a 1:1 relation.
  profile: { type: mongoose.Schema.Types.ObjectId, ref: 'UserProfile', default: null },
  department: { type: String, trim: true, default: '' },
  trade: { type: String, trim: true, default: '' },
  phone: { type: String, trim: true, default: '' },
  isActive: { type: Boolean, default: true }
}, { timestamps: true, versionKey: false });


userSchema.methods.toSafeJSON = function toSafeJSON() {
  const profile = this.profile && typeof this.profile === 'object' && this.profile.block !== undefined
    ? { block: this.profile.block || '', room: this.profile.room || '', wing: this.profile.wing || '' }
    : {};
  return {
    id: this._id.toString(), name: this.name, email: this.email, role: this.role,
    prn: this.prn || '', staffId: this.staffId || '', profile, department: this.department || '',
    trade: this.trade || '', phone: this.phone || '', createdAt: this.createdAt
  };
};

export default mongoose.model('User', userSchema);
