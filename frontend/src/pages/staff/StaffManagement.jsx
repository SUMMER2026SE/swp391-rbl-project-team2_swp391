import { useEffect, useState } from "react";
import { Edit, Plus, Search, Trash2, UserCog, X } from "lucide-react";
import {
  createStaff,
  deleteStaff,
  getStaffList,
  updateStaff,
} from "../../services/staffService";
import { getUsers } from "../../services/userService";

const STAFF_TYPES = [
  { value: "RECEPTIONIST", label: "Lễ tân" },
  { value: "PHARMACIST", label: "Dược sĩ" },
  { value: "LAB_TECHNICIAN", label: "Nhân viên xét nghiệm" },
  { value: "ADMIN", label: "Quản trị viên" },
];

const getStaffTypeLabel = (type) =>
  STAFF_TYPES.find((t) => t.value === type)?.label || type;

const EMPTY_FORM = {
  userId: "",
  staffCode: "",
  staffType: "RECEPTIONIST",
  position: "",
  status: "ACTIVE",
};

export default function StaffManagement() {
  const [staffList, setStaffList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [typeFilter, setTypeFilter] = useState("");

  const [users, setUsers] = useState([]);

  // Form state
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [formError, setFormError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  // Delete confirm
  const [deleteTarget, setDeleteTarget] = useState(null);

  /* ── Load Options ──────────────────────────────────────── */
  const fetchOptions = async () => {
    try {
      const userRes = await getUsers({ size: 100 });
      setUsers(userRes.data?.content ?? []);
    } catch (err) {
      console.error("Failed to load users", err);
    }
  };

  /* ── Fetch Staff ───────────────────────────────────────── */
  const fetchStaff = async () => {
    try {
      setLoading(true);
      const params = {};
      if (typeFilter) params.staffType = typeFilter;
      if (searchTerm) params.keyword = searchTerm;

      const res = await getStaffList(params);
      setStaffList(res.data?.content ?? []);
      setError("");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOptions();
  }, []);

  useEffect(() => {
    fetchStaff();
  }, [searchTerm, typeFilter]);

  /* ── Form handlers ─────────────────────────────────────── */
  const openCreate = () => {
    setFormData(EMPTY_FORM);
    setEditingId(null);
    setFormError("");
    setShowForm(true);
  };

  const openEdit = (staff) => {
    setFormData({
      userId: staff.userId || "",
      staffCode: staff.staffCode || "",
      staffType: staff.staffType || "RECEPTIONIST",
      position: staff.position || "",
      status: staff.status || "ACTIVE",
    });
    setEditingId(staff.staffId);
    setFormError("");
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    setEditingId(null);
    setFormData(EMPTY_FORM);
    setFormError("");
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      if (editingId) {
        await updateStaff(editingId, formData);
      } else {
        await createStaff(formData);
      }
      closeForm();
      await fetchStaff();
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  /* ── Delete handlers ───────────────────────────────────── */
  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteStaff(deleteTarget.staffId);
      setDeleteTarget(null);
      await fetchStaff();
    } catch (err) {
      setError(err.message);
      setDeleteTarget(null);
    }
  };

  return (
    <>
      {/* ── Page Header ────────────────────────────────── */}
      <div className="page-header">
        <div>
          <h1 className="page-title">
            <UserCog size={26} />
            Quản lý Nhân viên
          </h1>
          <p className="muted">Quản lý lễ tân, dược sĩ, nhân viên xét nghiệm và admin.</p>
        </div>
        <button className="primary-button" onClick={openCreate}>
          <Plus size={16} />
          Thêm nhân viên
        </button>
      </div>

      {/* ── Filters ────────────────────────────────────── */}
      <div className="search-bar" style={{ display: "flex", gap: "10px" }}>
        <div style={{ position: "relative", flex: 1, maxWidth: "420px" }}>
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Tìm theo tên hoặc mã nhân viên..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <select
          style={{ minHeight: "44px", borderRadius: "8px", border: "1px solid #d7dee8", padding: "0 12px", background: "#fff" }}
          value={typeFilter}
          onChange={(e) => setTypeFilter(e.target.value)}
        >
          <option value="">Tất cả loại nhân viên</option>
          {STAFF_TYPES.map((t) => (
            <option key={t.value} value={t.value}>
              {t.label}
            </option>
          ))}
        </select>
      </div>

      {error && <div className="error-box" style={{ marginBottom: 16 }}>{error}</div>}

      {/* ── Table ──────────────────────────────────────── */}
      <div className="table-wrapper">
        <table className="data-table">
          <thead>
            <tr>
              <th>Mã NV</th>
              <th>Họ và tên</th>
              <th>Email</th>
              <th>Loại nhân viên</th>
              <th>Chức vụ</th>
              <th>Trạng thái</th>
              <th style={{ textAlign: "center" }}>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} className="empty-row">Đang tải dữ liệu...</td>
              </tr>
            ) : staffList.length === 0 ? (
              <tr>
                <td colSpan={7} className="empty-row">Không tìm thấy nhân viên nào.</td>
              </tr>
            ) : (
              staffList.map((staff) => (
                <tr key={staff.staffId}>
                  <td className="cell-name">{staff.staffCode}</td>
                  <td><strong>{staff.fullName || "—"}</strong></td>
                  <td>{staff.email || "—"}</td>
                  <td>
                    <span className="status-badge badge-active">
                      {getStaffTypeLabel(staff.staffType)}
                    </span>
                  </td>
                  <td>{staff.position || "—"}</td>
                  <td>
                    <span className={`status-badge ${staff.status === "ACTIVE" ? "badge-active" : "badge-inactive"}`}>
                      {staff.status === "ACTIVE" ? "Đang làm việc" : "Đã nghỉ"}
                    </span>
                  </td>
                  <td>
                    <div className="action-group">
                      <button className="icon-button" onClick={() => openEdit(staff)} title="Chỉnh sửa">
                        <Edit size={15} />
                      </button>
                      <button className="icon-button btn-danger" onClick={() => setDeleteTarget(staff)} title="Xóa">
                        <Trash2 size={15} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* ── Modal Thêm/Sửa ─────────────────────────────── */}
      {showForm && (
        <div className="modal-overlay" onClick={closeForm}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()} style={{ width: "560px" }}>
            <div className="modal-header">
              <h2>{editingId ? "Cập nhật hồ sơ nhân viên" : "Thêm nhân viên mới"}</h2>
              <button className="icon-button" onClick={closeForm}><X size={18} /></button>
            </div>

            <form className="form-stack" onSubmit={handleSubmit} style={{ maxHeight: "70vh", overflowY: "auto", paddingRight: 10 }}>
              {formError && <div className="error-box">{formError}</div>}

              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
                <div className="field">
                  <label>Mã nhân viên *</label>
                  <input name="staffCode" value={formData.staffCode} onChange={handleChange} required />
                </div>

                <div className="field">
                  <label>Tài khoản User liên kết *</label>
                  <select name="userId" value={formData.userId} onChange={handleChange} required>
                    <option value="">-- Chọn User --</option>
                    {users.map((u) => (
                      <option key={u.userId} value={u.userId}>{u.fullName} ({u.email})</option>
                    ))}
                  </select>
                </div>

                <div className="field">
                  <label>Loại nhân viên *</label>
                  <select name="staffType" value={formData.staffType} onChange={handleChange} required>
                    {STAFF_TYPES.map((t) => (
                      <option key={t.value} value={t.value}>{t.label}</option>
                    ))}
                  </select>
                </div>

                <div className="field">
                  <label>Chức vụ</label>
                  <input name="position" value={formData.position} onChange={handleChange} placeholder="VD: Trưởng lễ tân" />
                </div>

                {editingId && (
                  <div className="field">
                    <label>Trạng thái</label>
                    <select name="status" value={formData.status} onChange={handleChange}>
                      <option value="ACTIVE">Đang làm việc</option>
                      <option value="INACTIVE">Đã thôi việc</option>
                    </select>
                  </div>
                )}
              </div>

              <div className="form-actions" style={{ marginTop: 24 }}>
                <button type="button" className="secondary-button" onClick={closeForm}>Hủy</button>
                <button type="submit" className="primary-button" disabled={submitting}>
                  {submitting ? "Đang xử lý..." : "Lưu hồ sơ"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ── Modal Xóa ──────────────────────────────────── */}
      {deleteTarget && (
        <div className="modal-overlay" onClick={() => setDeleteTarget(null)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Xác nhận xóa</h2>
              <button className="icon-button" onClick={() => setDeleteTarget(null)}><X size={18} /></button>
            </div>
            <p>Bạn có chắc chắn muốn xóa nhân viên <strong>{deleteTarget.fullName || deleteTarget.staffCode}</strong> không?</p>
            <div className="form-actions">
              <button className="secondary-button" onClick={() => setDeleteTarget(null)}>Hủy</button>
              <button className="danger-button" onClick={confirmDelete}>Xóa</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
