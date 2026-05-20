import axiosClient from "./axiosClient";

export const getStaffList = (params) => axiosClient.get("/staff", { params });

export const getStaffById = (id) => axiosClient.get(`/staff/${id}`);

export const createStaff = (payload) => axiosClient.post("/staff", payload);

export const updateStaff = (id, payload) => axiosClient.put(`/staff/${id}`, payload);

export const deleteStaff = (id) => axiosClient.delete(`/staff/${id}`);
