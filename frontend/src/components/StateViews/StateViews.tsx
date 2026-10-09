import "./StateViews.css";

type EmptyProps = {
  icon?: string;
  title: string;
  description?: string;
  action?: { label: string; onClick: () => void };
};

export function EmptyState({ icon = "📭", title, description, action }: EmptyProps) {
  return (
    <div className="state-view">
      <div className="state-view__icon">{icon}</div>
      <h3 className="state-view__title">{title}</h3>
      {description && <p className="state-view__desc">{description}</p>}
      {action && (
        <button className="state-view__btn" onClick={action.onClick}>
          {action.label}
        </button>
      )}
    </div>
  );
}

type ErrorProps = {
  message: string;
  onRetry?: () => void;
};

export function ErrorState({ message, onRetry }: ErrorProps) {
  return (
    <div className="state-view state-view--error">
      <div className="state-view__icon">⚠️</div>
      <h3 className="state-view__title">{message}</h3>
      <p className="state-view__desc">
        Verifique sua conexão ou se o servidor está no ar.
      </p>
      {onRetry && (
        <button className="state-view__btn" onClick={onRetry}>
          Tentar de novo
        </button>
      )}
    </div>
  );
}