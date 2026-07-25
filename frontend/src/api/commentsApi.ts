import api from "./api";

export interface Comment {
  id: number;
  authorId: number;
  authorName: string;
  authorEmail: string;
  content: string;
  createdAt: string;
  ownsVehicle: boolean;
  replies: Comment[];
}

export interface CreateCommentDTO {
  content: string;
  parentCommentId?: number;
}

export const getComments = async (issueId: number): Promise<Comment[]> => {
  const res = await api.get(`/chronic-issues/${issueId}/comments`);
  return res.data;
};

export const createComment = async (
  issueId: number,
  dto: CreateCommentDTO
): Promise<number> => {
  const res = await api.post(`/chronic-issues/${issueId}/comments`, dto);
  return res.data;
};

export const deleteComment = async (
  issueId: number,
  commentId: number
): Promise<void> => {
  await api.delete(`/chronic-issues/${issueId}/comments/${commentId}`);
};