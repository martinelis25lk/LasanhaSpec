import { useEffect, useState } from "react";
import { getComments, createComment, deleteComment, type Comment } from "../../api/commentsApi";
import { useCurrentUser } from "../../hooks/useCurrentUser";
import "./CommentsSection.css";

function timeAgo(iso: string): string {
  const diffMs = Date.now() - new Date(iso).getTime();
  const mins = Math.floor(diffMs / 60000);
  if (mins < 1) return "agora";
  if (mins < 60) return `${mins}min atrás`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours}h atrás`;
  return `${Math.floor(hours / 24)}d atrás`;
}

interface CommentItemProps {
  comment: Comment;
  issueId: number;
  currentUserEmail?: string;
  isAdmin: boolean;
  onReplySubmit: (parentId: number, content: string) => Promise<void>;
  onDelete: (commentId: number) => Promise<void>;
  isReply?: boolean;
}

function CommentItem({
  comment, issueId, currentUserEmail, isAdmin, onReplySubmit, onDelete, isReply,
}: CommentItemProps) {
  const [showReplyBox, setShowReplyBox] = useState(false);
  const [replyText, setReplyText] = useState("");
  const [sending, setSending] = useState(false);

  const canDelete = isAdmin || comment.authorEmail === currentUserEmail;

  async function handleReply() {
    if (!replyText.trim()) return;
    setSending(true);
    try {
      await onReplySubmit(comment.id, replyText.trim());
      setReplyText("");
      setShowReplyBox(false);
    } finally {
      setSending(false);
    }
  }

  return (
    <div className={isReply ? "comment-item comment-reply" : "comment-item"}>
      <div className="comment-header">
        <span className="comment-author">
          {comment.authorName}
          {comment.ownsVehicle && <span className="comment-badge">dono confirmado</span>}
        </span>
        <span className="comment-time">{timeAgo(comment.createdAt)}</span>
      </div>
      <p className="comment-content">{comment.content}</p>
      <div className="comment-actions">
        {!isReply && (
          <button className="comment-action-btn" onClick={() => setShowReplyBox((v) => !v)}>
            Responder
          </button>
        )}
        {canDelete && (
          <button
            className="comment-action-btn comment-delete"
            onClick={() => { if (confirm("Apagar este comentário?")) onDelete(comment.id); }}
          >
            Apagar
          </button>
        )}
      </div>

      {showReplyBox && (
        <div className="comment-reply-box">
          <textarea
            placeholder="Escreva uma resposta..."
            value={replyText}
            onChange={(e) => setReplyText(e.target.value)}
          />
          <div className="comment-reply-actions">
            <button onClick={() => setShowReplyBox(false)}>Cancelar</button>
            <button className="comment-reply-submit" onClick={handleReply} disabled={sending}>
              {sending ? "Enviando..." : "Responder"}
            </button>
          </div>
        </div>
      )}

      {comment.replies?.length > 0 && (
        <div className="comment-replies">
          {comment.replies.map((reply) => (
            <CommentItem
              key={reply.id}
              comment={reply}
              issueId={issueId}
              currentUserEmail={currentUserEmail}
              isAdmin={isAdmin}
              onReplySubmit={onReplySubmit}
              onDelete={onDelete}
              isReply
            />
          ))}
        </div>
      )}
    </div>
  );
}

export default function CommentSection({ issueId }: { issueId: number }) {
  const user = useCurrentUser();
  const isAdmin = user?.role === "ROLE_ADMIN";

  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(true);
  const [newComment, setNewComment] = useState("");
  const [posting, setPosting] = useState(false);

  function load() {
    setLoading(true);
    getComments(issueId).then(setComments).finally(() => setLoading(false));
  }

  useEffect(() => { load(); }, [issueId]);

  async function handleSubmit() {
    if (!newComment.trim()) return;
    setPosting(true);
    try {
      await createComment(issueId, { content: newComment.trim() });
      setNewComment("");
      load();
    } catch (e: any) {
      alert(e?.response?.data?.message ?? "Erro ao comentar.");
    } finally {
      setPosting(false);
    }
  }

  async function handleReplySubmit(parentId: number, content: string) {
    try {
      await createComment(issueId, { content, parentCommentId: parentId });
      load();
    } catch (e: any) {
      alert(e?.response?.data?.message ?? "Erro ao responder.");
    }
  }

  async function handleDelete(commentId: number) {
    try {
      await deleteComment(issueId, commentId);
      load();
    } catch (e: any) {
      alert(e?.response?.data?.message ?? "Erro ao apagar.");
    }
  }

  return (
    <div className="idp-section">
      <h2>💬 Discussão ({comments.length})</h2>

      <div className="comment-new-box">
        <textarea
          placeholder="Compartilhe sua experiência com esse problema..."
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
        />
        <button className="comment-submit-btn" onClick={handleSubmit} disabled={posting}>
          {posting ? "Enviando..." : "Comentar"}
        </button>
      </div>

      {loading ? (
        <p className="comment-loading">Carregando comentários...</p>
      ) : comments.length === 0 ? (
        <p className="comment-empty">Ainda não há comentários. Seja o primeiro a comentar.</p>
      ) : (
        <div className="comment-list">
          {comments.map((c) => (
            <CommentItem
              key={c.id}
              comment={c}
              issueId={issueId}
              currentUserEmail={user?.email}
              isAdmin={isAdmin}
              onReplySubmit={handleReplySubmit}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}
    </div>
  );
}