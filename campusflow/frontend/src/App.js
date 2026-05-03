import { useEffect, useMemo, useState, useRef } from "react";
import "./App.css";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL
  || (process.env.NODE_ENV === "development" ? "" : "http://localhost:8080");

const demoAccounts = [
  {
    label: "Admin demo",
    email: "admin.demo@ada.edu.az",
    password: "CampusFlow123!",
  },
  {
    label: "Moderator demo",
    email: "moderator.demo@ada.edu.az",
    password: "CampusFlow123!",
  },
  {
    label: "Student demo",
    email: "student.demo@ada.edu.az",
    password: "CampusFlow123!",
  },
];

const reactionChoices = ["Like", "Support", "Celebrate"];

const campusLocations = [
  {
    name: "ADA University Main Building",
    description: "Quick link for campus navigation and meeting points.",
    query: "ADA University Baku",
  },
  {
    name: "Innovation Lab",
    description: "Use for product nights, design sessions, and team meetups.",
    query: "ADA University Innovation Lab Baku",
  },
  {
    name: "Library",
    description: "Great spot for study groups and quiet work sessions.",
    query: "ADA University Library Baku",
  },
];

const publicNavigation = [
  { id: "auth", label: "Home" },
  { id: "menu", label: "Menu" },
];

const privateNavigation = [
  { id: "dashboard", label: "Dashboard" },
  { id: "feed", label: "Feed" },
  { id: "events", label: "Events" },
  { id: "groups", label: "Groups" },
  { id: "notifications", label: "Notifications" },
  { id: "profile", label: "Profile" },
];

const emptyAuthForm = {
  username: "",
  email: "",
  password: "",
  otpCode: "",
};

const emptyResetForm = {
  email: "",
  otpCode: "",
  newPassword: "",
  confirmPassword: "",
};

const emptyPostForm = {
  title: "",
  content: "",
  type: "ANNOUNCEMENT",
};

const emptyEventForm = {
  title: "",
  description: "",
  eventDate: "",
  location: "",
  capacity: 40,
};

const emptyStudyGroupForm = {
  name: "",
  topic: "",
  course: "",
  capacity: 6,
};

const emptyBackendProfileForm = {
  username: "",
};

const emptyPreferenceProfile = {
  major: "",
  interests: "",
  profilePhotoUrl: "",
  privacy: "Campus only",
  showEmail: false,
  notificationsEnabled: true,
  theme: "light",
};

const emptyMessageForm = {
  content: "",
};

const emptyResourceForm = {
  title: "",
  url: "",
};

const pathPuzzleLevels = [
  {
    id: "route-1",
    name: "Warm-up Route",
    difficulty: 1,
    size: 4,
    description: "A simpler 4x4 starter puzzle. Fill every tile while visiting the corners in order.",
    checkpoints: [
      { label: 1, x: 0, y: 0 },
      { label: 2, x: 3, y: 0 },
      { label: 3, x: 3, y: 3 },
      { label: 4, x: 0, y: 3 },
    ],
    solution: [
      { x: 0, y: 0 },
      { x: 1, y: 0 },
      { x: 2, y: 0 },
      { x: 3, y: 0 },
      { x: 3, y: 1 },
      { x: 2, y: 1 },
      { x: 1, y: 1 },
      { x: 0, y: 1 },
      { x: 0, y: 2 },
      { x: 1, y: 2 },
      { x: 2, y: 2 },
      { x: 3, y: 2 },
      { x: 3, y: 3 },
      { x: 2, y: 3 },
      { x: 1, y: 3 },
      { x: 0, y: 3 },
    ],
  },
  {
    id: "route-2",
    name: "Studio Loop",
    difficulty: 2,
    size: 4,
    description: "A 4x4 route with inner turns. Any full-grid answer works if the checkpoints stay in order.",
    checkpoints: [
      { label: 1, x: 0, y: 1 },
      { label: 2, x: 3, y: 0 },
      { label: 3, x: 3, y: 2 },
      { label: 4, x: 0, y: 2 },
    ],
    solution: [
      { x: 0, y: 1 },
      { x: 0, y: 0 },
      { x: 1, y: 0 },
      { x: 2, y: 0 },
      { x: 3, y: 0 },
      { x: 3, y: 1 },
      { x: 2, y: 1 },
      { x: 1, y: 1 },
      { x: 1, y: 2 },
      { x: 2, y: 2 },
      { x: 3, y: 2 },
      { x: 3, y: 3 },
      { x: 2, y: 3 },
      { x: 1, y: 3 },
      { x: 0, y: 3 },
      { x: 0, y: 2 },
    ],
  },
  {
    id: "route-3",
    name: "Corner Circuit",
    difficulty: 3,
    size: 4,
    description: "A final 4x4 puzzle with one extra checkpoint but still the same simple full-grid rule.",
    checkpoints: [
      { label: 1, x: 3, y: 0 },
      { label: 2, x: 0, y: 1 },
      { label: 3, x: 3, y: 2 },
      { label: 4, x: 0, y: 3 },
      { label: 5, x: 3, y: 3 },
    ],
    solution: [
      { x: 3, y: 0 },
      { x: 2, y: 0 },
      { x: 1, y: 0 },
      { x: 0, y: 0 },
      { x: 0, y: 1 },
      { x: 1, y: 1 },
      { x: 2, y: 1 },
      { x: 3, y: 1 },
      { x: 3, y: 2 },
      { x: 2, y: 2 },
      { x: 1, y: 2 },
      { x: 0, y: 2 },
      { x: 0, y: 3 },
      { x: 1, y: 3 },
      { x: 2, y: 3 },
      { x: 3, y: 3 },
    ],
  },
];

function coordsKey(x, y) {
  return `${x}-${y}`;
}

function formatDateTime(value) {
  if (!value) {
    return "No date set";
  }

  return new Intl.DateTimeFormat("en", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

function normalizeDateTime(value) {
  if (!value) {
    return value;
  }

  return value.length === 16 ? `${value}:00` : value;
}

function matchesQuery(values, query) {
  if (!query.trim()) {
    return true;
  }

  const normalizedQuery = query.trim().toLowerCase();
  return values.some((value) =>
    String(value || "").toLowerCase().includes(normalizedQuery));
}

function canCreateModerationContent(user) {
  return user && (user.role === "ADMIN" || user.role === "MODERATOR");
}

function canOpenGroupChat(group, user, joinStatuses) {
  if (!user) {
    return false;
  }

  return group.creatorId === user.userId || joinStatuses[group.id] === "ACCEPTED";
}

function formatRole(role) {
  if (!role) {
    return "Member";
  }

  return role.charAt(0) + role.slice(1).toLowerCase();
}

function formatDuration(totalSeconds) {
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${minutes}:${String(seconds).padStart(2, "0")}`;
}

function isAdjacentCell(firstCell, secondCell) {
  if (!firstCell || !secondCell) {
    return false;
  }

  return Math.abs(firstCell.x - secondCell.x) + Math.abs(firstCell.y - secondCell.y) === 1;
}

function getVisitedCheckpointLabels(path, checkpointMap) {
  return path
    .map((cell) => checkpointMap.get(coordsKey(cell.x, cell.y)))
    .filter(Boolean);
}

function getNextRequiredCheckpoint(level, path, checkpointMap) {
  const nextLabel = getVisitedCheckpointLabels(path, checkpointMap).length + 1;
  return level.checkpoints.find((checkpoint) => checkpoint.label === nextLabel) || null;
}

function didVisitCheckpointsInOrder(level, path, checkpointMap) {
  const visitedLabels = getVisitedCheckpointLabels(path, checkpointMap);
  return visitedLabels.length === level.checkpoints.length
    && visitedLabels.every((label, index) => label === index + 1);
}

function isPuzzleGridFilled(level, path) {
  return path.length === level.size * level.size;
}

function calculatePathPuzzleScore(level, durationSeconds, movesUsed, hintsUsed) {
  const optimalMoves = Math.max(0, (level.size * level.size) - 1);
  const movePenalty = Math.max(0, movesUsed - optimalMoves) * 8;
  const timePenalty = Math.min(140, durationSeconds * 2);
  const hintPenalty = hintsUsed * 24;
  const baseScore = 220 + level.difficulty * 110;

  return Math.max(80, Math.round(baseScore - movePenalty - timePenalty - hintPenalty));
}

function buildCalendarLink(event) {
  const startDate = new Date(event.eventDate);
  const endDate = new Date(startDate.getTime() + 60 * 60 * 1000);
  const formatCalendarDate = (date) => date.toISOString().replace(/[-:]/g, "").split(".")[0] + "Z";

  const params = new URLSearchParams({
    action: "TEMPLATE",
    text: event.title,
    details: event.description,
    location: event.location,
    dates: `${formatCalendarDate(startDate)}/${formatCalendarDate(endDate)}`,
  });

  return `https://calendar.google.com/calendar/render?${params.toString()}`;
}

function buildMapLink(query) {
  return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(query)}`;
}

function getStorageKey(prefix, email) {
  return `${prefix}:${email}`;
}

function readStoredJson(key, fallback) {
  try {
    const raw = window.localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch (_error) {
    return fallback;
  }
}

function App() {
  const [activeView, setActiveView] = useState("auth");
  const [authPage, setAuthPage] = useState("login");
  const [authForm, setAuthForm] = useState(emptyAuthForm);
  const [resetForm, setResetForm] = useState(emptyResetForm);
  const [resetStep, setResetStep] = useState("request");
  const [status, setStatus] = useState({
    type: "idle",
    message: "Log in to start using CampusFlow.",
  });
  const [workspaceNotice, setWorkspaceNotice] = useState({
    type: "idle",
    message: "Sign in to sync your campus feed, events, and study groups.",
  });
  const [loadingAction, setLoadingAction] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [feedPosts, setFeedPosts] = useState([]);
  const [events, setEvents] = useState([]);
  const [studyGroups, setStudyGroups] = useState([]);
  const [joinStatuses, setJoinStatuses] = useState({});
  const [creatorRequests, setCreatorRequests] = useState({});
  const [selectedGroupId, setSelectedGroupId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [messageStatus, setMessageStatus] = useState("Select a study group to open chat.");
  const [unreadGroups, setUnreadGroups] = useState(new Set());
  const [typingByGroup, setTypingByGroup] = useState({});
  const [readReceiptsByMessageId, setReadReceiptsByMessageId] = useState({});
  const [feedSearch, setFeedSearch] = useState("");
  const [feedTypeFilter, setFeedTypeFilter] = useState("ALL");
  const [eventSearch, setEventSearch] = useState("");
  const [groupSearch, setGroupSearch] = useState("");
  const [postForm, setPostForm] = useState(emptyPostForm);
  const [eventForm, setEventForm] = useState(emptyEventForm);
  const [studyGroupForm, setStudyGroupForm] = useState(emptyStudyGroupForm);
  const [backendProfileForm, setBackendProfileForm] = useState(emptyBackendProfileForm);
  const [profilePreferences, setProfilePreferences] = useState(emptyPreferenceProfile);
  const [profilePhotoFile, setProfilePhotoFile] = useState(null);
  const [profilePhotoPreviewUrl, setProfilePhotoPreviewUrl] = useState("");
  const [profilePhotoUploadStatus, setProfilePhotoUploadStatus] = useState("");
  const [auditLogEntries, setAuditLogEntries] = useState([]);
  const [auditLogStatus, setAuditLogStatus] = useState("Audit log is available to moderators and admins.");
  const [moderationQueueEntries, setModerationQueueEntries] = useState([]);
  const [moderationQueueStatus, setModerationQueueStatus] = useState("Moderation queue is available to moderators and admins.");
  const [serverNotifications, setServerNotifications] = useState([]);
  const [serverNotificationsStatus, setServerNotificationsStatus] = useState("Notifications are stored in your account.");
  const [profileCompleteness, setProfileCompleteness] = useState(null);
  const [profileCompletenessStatus, setProfileCompletenessStatus] = useState("");
  const [pushStatus, setPushStatus] = useState("");
  const [messageForm, setMessageForm] = useState(emptyMessageForm);
  const [chatFile, setChatFile] = useState(null);
  const [chatFilePreviewUrl, setChatFilePreviewUrl] = useState("");
  const [chatFileStatus, setChatFileStatus] = useState("");
  const [resourceForm, setResourceForm] = useState(emptyResourceForm);
  const [sharedResources, setSharedResources] = useState({});
  const [postInteractions, setPostInteractions] = useState({});
  const [rsvpStateByEvent, setRsvpStateByEvent] = useState({});
  const [dailyQuiz, setDailyQuiz] = useState({
    quizDate: null,
    questions: [],
  });
  const [quizLeaderboard, setQuizLeaderboard] = useState([]);
  const [quizAnswers, setQuizAnswers] = useState({});
  const [quizQuestionIndex, setQuizQuestionIndex] = useState(0);
  const [quizResult, setQuizResult] = useState(null);
  const [quizStatus, setQuizStatus] = useState({
    type: "idle",
    message: "Answer five quick OOP and system design questions to earn points and climb the leaderboard.",
  });
  const [selectedPuzzleLevelId, setSelectedPuzzleLevelId] = useState(pathPuzzleLevels[0].id);
  const [pathPuzzleLeaderboard, setPathPuzzleLeaderboard] = useState([]);
  const [puzzlePath, setPuzzlePath] = useState([]);
  const [puzzleStarted, setPuzzleStarted] = useState(false);
  const [puzzleReadyToSubmit, setPuzzleReadyToSubmit] = useState(false);
  const [puzzleElapsedSeconds, setPuzzleElapsedSeconds] = useState(0);
  const [puzzleStartedAt, setPuzzleStartedAt] = useState(null);
  const [puzzleHintsUsed, setPuzzleHintsUsed] = useState(0);
  const [puzzleStatus, setPuzzleStatus] = useState({
    type: "idle",
    message: "Choose a route and press Start to fill the full 4x4 grid.",
  });
  const [puzzleHintCellKey, setPuzzleHintCellKey] = useState(null);
  const [pathPuzzleResult, setPathPuzzleResult] = useState(null);
  const [puzzleShareStatus, setPuzzleShareStatus] = useState("");
  const [showPuzzleSolution, setShowPuzzleSolution] = useState(false);

  const socketRef = useRef(null);
  const typingTimeoutRef = useRef(null);

  const selectedGroup = studyGroups.find((group) => group.id === selectedGroupId) || null;
  const selectedGroupResources = sharedResources[selectedGroupId] || [];
  const filteredFeed = feedPosts.filter((post) => (
    (feedTypeFilter === "ALL" || post.type === feedTypeFilter)
    && matchesQuery(
      [post.title, post.content, post.authorUsername, post.type],
      feedSearch,
    )
  ));
  const filteredEvents = events.filter((event) => matchesQuery(
    [event.title, event.description, event.location, event.status],
    eventSearch,
  ));
  const filteredStudyGroups = studyGroups.filter((group) => matchesQuery(
    [group.name, group.topic, group.course, group.status],
    groupSearch,
  ));
  const currentQuizQuestion = dailyQuiz.questions[quizQuestionIndex] || null;
  const answeredQuizCount = dailyQuiz.questions.filter(
    (question) => quizAnswers[question.id] !== undefined,
  ).length;
  const currentPuzzleLevel = useMemo(
    () => pathPuzzleLevels.find((level) => level.id === selectedPuzzleLevelId) || pathPuzzleLevels[0],
    [selectedPuzzleLevelId],
  );
  const currentPuzzleCheckpointMap = useMemo(
    () => new Map(
      currentPuzzleLevel.checkpoints.map((checkpoint) => [
        coordsKey(checkpoint.x, checkpoint.y),
        checkpoint.label,
      ]),
    ),
    [currentPuzzleLevel],
  );
  const puzzlePathKeys = useMemo(
    () => new Set(puzzlePath.map((cell) => coordsKey(cell.x, cell.y))),
    [puzzlePath],
  );
  const currentPuzzleSolutionKeys = useMemo(
    () => new Set(currentPuzzleLevel.solution.map((cell) => coordsKey(cell.x, cell.y))),
    [currentPuzzleLevel],
  );
  const currentPuzzleSolutionStepMap = useMemo(
    () => new Map(
      currentPuzzleLevel.solution.map((cell, index) => [coordsKey(cell.x, cell.y), index + 1]),
    ),
    [currentPuzzleLevel],
  );
  const puzzleMovesUsed = Math.max(0, puzzlePath.length - 1);
  const puzzleTotalCells = currentPuzzleLevel.size * currentPuzzleLevel.size;
  const remainingPuzzleCells = Math.max(0, puzzleTotalCells - puzzlePath.length);
  const notifications = useMemo(() => {
    if (!currentUser || !profilePreferences.notificationsEnabled) {
      return [];
    }

    const items = [];

    feedPosts.slice(0, 3).forEach((post) => {
      items.push({
        id: `feed-${post.id}`,
        title: post.type === "ANNOUNCEMENT" ? "New announcement" : "Feed update",
        detail: `${post.title} by ${post.authorUsername}`,
        time: post.createdAt,
        tone: "feed",
      });
    });

    events
      .filter((event) => {
        const diffDays = (new Date(event.eventDate).getTime() - Date.now()) / (1000 * 60 * 60 * 24);
        return diffDays >= 0 && diffDays <= 7;
      })
      .forEach((event) => {
        items.push({
          id: `event-${event.id}`,
          title: "Event reminder",
          detail: `${event.title} is coming up on ${formatDateTime(event.eventDate)}.`,
          time: event.eventDate,
          tone: "event",
        });
      });

    Object.entries(creatorRequests).forEach(([groupId, requests]) => {
      requests.forEach((requestItem) => {
        items.push({
          id: `request-${groupId}-${requestItem.id}`,
          title: "Join request pending",
          detail: `Study group #${groupId} has a request from user #${requestItem.userId}.`,
          time: requestItem.requestedAt,
          tone: "group",
        });
      });
    });

    Object.entries(joinStatuses).forEach(([groupId, joinStatus]) => {
      if (joinStatus === "PENDING" || joinStatus === "ACCEPTED" || joinStatus === "REJECTED") {
        items.push({
          id: `join-status-${groupId}`,
          title: "Study group update",
          detail: `Your status for group #${groupId} is ${joinStatus.toLowerCase()}.`,
          time: new Date().toISOString(),
          tone: joinStatus === "ACCEPTED" ? "success" : "group",
        });
      }
    });

    Object.entries(postInteractions).forEach(([postId, interaction]) => {
      if (interaction?.reported) {
        items.push({
          id: `report-${postId}`,
          title: "Content reported",
          detail: `Post #${postId} has been flagged for moderation review.`,
          time: interaction.reportedAt || new Date().toISOString(),
          tone: "warning",
        });
      }
    });

    return items.sort((first, second) => new Date(second.time) - new Date(first.time));
  }, [creatorRequests, currentUser, events, feedPosts, joinStatuses, postInteractions, profilePreferences.notificationsEnabled]);

  const request = async (path, options = {}) => {
    const {
      method = "GET",
      body,
      auth = true,
      allowNotFound = false,
    } = options;

    const headers = {};
    if (body !== undefined) {
      headers["Content-Type"] = "application/json";
    }

    if (auth) {
      const token = window.localStorage.getItem("campusflowToken");
      if (token) {
        headers.Authorization = `Bearer ${token}`;
      }
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    });

    const rawText = await response.text();
    let data = null;

    if (rawText) {
      try {
        data = JSON.parse(rawText);
      } catch (_error) {
        data = rawText;
      }
    }

    if (!response.ok) {
      if (allowNotFound && response.status === 404) {
        return null;
      }

      throw new Error(
        data?.message
          || data?.error
          || (typeof data === "string" ? data : "")
          || `Request failed with status ${response.status}.`,
      );
    }

    return data;
  };

  const base64UrlToUint8Array = (base64UrlString) => {
    const padding = "=".repeat((4 - (base64UrlString.length % 4)) % 4);
    const base64 = (base64UrlString + padding).replace(/-/g, "+").replace(/_/g, "/");
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; i += 1) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  };

  const enableWebPush = async () => {
    if (!currentUser) {
      setPushStatus("Log in first to enable notifications.");
      return;
    }
    if (!("serviceWorker" in navigator) || !("PushManager" in window)) {
      setPushStatus("This browser does not support Web Push.");
      return;
    }

    setPushStatus("Requesting permission...");
    const permission = await Notification.requestPermission();
    if (permission !== "granted") {
      setPushStatus("Notification permission was not granted.");
      return;
    }

    setPushStatus("Preparing subscription...");
    const keyData = await request("/api/v1/push/vapid-public-key");
    const publicKey = keyData?.publicKey;
    if (!publicKey) {
      setPushStatus("Server VAPID public key is not configured.");
      return;
    }

    const registration = await navigator.serviceWorker.ready;
    const existing = await registration.pushManager.getSubscription();
    const subscription = existing || await registration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: base64UrlToUint8Array(publicKey),
    });

    await request("/api/v1/push/subscribe", {
      method: "POST",
      body: subscription.toJSON(),
    });

    setPushStatus("Web Push enabled for this browser.");
  };

  const sendTestWebPush = async () => {
    if (!currentUser) {
      return;
    }
    setPushStatus("Sending test push...");
    try {
      const result = await request("/api/v1/push/test", {
        method: "POST",
        body: { message: "Test notification from CampusFlow" },
      });
      setPushStatus(result?.message || "Push sent (if subscribed).");
    } catch (error) {
      setPushStatus(error.message || "Could not send test push.");
    }
  };

  const connectToGroupSocket = (groupId) => {
    if (socketRef.current) {
      socketRef.current.close();
      socketRef.current = null;
    }

    if (!groupId) {
      return;
    }

    const token = window.localStorage.getItem("campusflowToken");
    if (!token) {
      return;
    }

    const httpBaseUrl = API_BASE_URL || "http://localhost:8080";
    const wsBaseUrl = httpBaseUrl.replace(/^http/, "ws");
    const url = `${wsBaseUrl}/ws/study-groups/${groupId}?token=${encodeURIComponent(token)}`;

    const ws = new WebSocket(url);

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        if (!data || typeof data !== "object") {
          return;
        }

        if (data.type === "message") {
          setMessages((prev) => {
            if (prev.some((message) => message.id === data.id)) {
              return prev;
            }
            return [...prev, data];
          });

          setSelectedGroupId((currentId) => {
            if (currentId !== data.studyGroupId) {
              setUnreadGroups((prevUnread) => new Set([...prevUnread, data.studyGroupId]));
            }
            return currentId;
          });

          setSelectedGroupId((currentId) => {
            if (currentId === data.studyGroupId && data.id) {
              try {
                ws.send(JSON.stringify({ type: "read", messageId: data.id }));
              } catch (_e) {
                // ignore
              }
            }
            return currentId;
          });
        } else if (data.type === "typing") {
          const group = data.studyGroupId;
          if (group == null) {
            return;
          }
          setTypingByGroup((current) => {
            const existing = current[group] || {};
            const next = { ...existing };
            if (data.isTyping) {
              next[data.userId] = data.username || "Someone";
            } else {
              delete next[data.userId];
            }
            return { ...current, [group]: next };
          });
        } else if (data.type === "read") {
          if (!data.messageId || !data.userId) {
            return;
          }
          setReadReceiptsByMessageId((current) => {
            const existing = current[data.messageId] || [];
            if (existing.includes(data.userId)) {
              return current;
            }
            return { ...current, [data.messageId]: [...existing, data.userId] };
          });
        }
      } catch (_e) {
        // ignore parse errors
      }
    };

    socketRef.current = ws;
  };

  const disconnectFromGroupSocket = () => {
    if (typingTimeoutRef.current) {
      clearTimeout(typingTimeoutRef.current);
      typingTimeoutRef.current = null;
    }
    if (socketRef.current) {
      try {
        socketRef.current.close();
      } catch (_e) {
        // ignore
      }
      socketRef.current = null;
    }
  };

  const sendTyping = (isTyping) => {
    if (!selectedGroupId || !socketRef.current || socketRef.current.readyState !== WebSocket.OPEN) {
      return;
    }
    try {
      socketRef.current.send(JSON.stringify({ type: "typing", isTyping }));
    } catch (_e) {
      // ignore
    }
  };

  const handleMessageInputChange = (event) => {
    handleFormChange(setMessageForm)(event);

    sendTyping(true);
    if (typingTimeoutRef.current) {
      clearTimeout(typingTimeoutRef.current);
    }
    typingTimeoutRef.current = setTimeout(() => {
      sendTyping(false);
      typingTimeoutRef.current = null;
    }, 1500);
  };

  useEffect(() => {
    if (activeView !== "groups") {
      disconnectFromGroupSocket();
    }
  }, [activeView]);

  useEffect(() => {
    return () => disconnectFromGroupSocket();
  }, []);

  useEffect(() => {
    if (!selectedGroupId || !socketRef.current || socketRef.current.readyState !== WebSocket.OPEN) {
      return;
    }
    messages.forEach((message) => {
      if (!message?.id || message.studyGroupId !== selectedGroupId) {
        return;
      }
      try {
        socketRef.current.send(JSON.stringify({ type: "read", messageId: message.id }));
      } catch (_e) {
        // ignore
      }
    });
  }, [messages, selectedGroupId]);

  const updateStoredUser = (nextUser) => {
    setCurrentUser(nextUser);
    if (nextUser) {
      window.localStorage.setItem("campusflowUser", JSON.stringify(nextUser));
    } else {
      window.localStorage.removeItem("campusflowUser");
    }
  };

  const loadAuditLog = async () => {
    if (!currentUser || (currentUser.role !== "MODERATOR" && currentUser.role !== "ADMIN")) {
      setAuditLogEntries([]);
      setAuditLogStatus("Audit log is available to moderators and admins.");
      return;
    }

    setAuditLogStatus("Loading audit log...");
    try {
      const data = await request("/api/v1/moderation/audit?limit=50");
      setAuditLogEntries(Array.isArray(data) ? data : []);
      setAuditLogStatus("Audit log synced.");
    } catch (error) {
      setAuditLogEntries([]);
      setAuditLogStatus(error.message || "Could not load audit log.");
    }
  };

  const loadModerationQueue = async () => {
    if (!currentUser || (currentUser.role !== "MODERATOR" && currentUser.role !== "ADMIN")) {
      setModerationQueueEntries([]);
      setModerationQueueStatus("Moderation queue is available to moderators and admins.");
      return;
    }

    setModerationQueueStatus("Loading moderation queue...");
    try {
      const data = await request("/api/v1/moderation/queue?limit=50");
      setModerationQueueEntries(Array.isArray(data) ? data : []);
      setModerationQueueStatus("Moderation queue synced.");
    } catch (error) {
      setModerationQueueEntries([]);
      setModerationQueueStatus(error.message || "Could not load moderation queue.");
    }
  };

  const loadServerNotifications = async () => {
    if (!currentUser) {
      setServerNotifications([]);
      setServerNotificationsStatus("Sign in to see notifications.");
      return;
    }

    setServerNotificationsStatus("Loading notifications...");
    try {
      const data = await request("/api/v1/notifications?limit=50");
      setServerNotifications(Array.isArray(data) ? data : []);
      setServerNotificationsStatus("Notifications synced.");
    } catch (error) {
      setServerNotifications([]);
      setServerNotificationsStatus(error.message || "Could not load notifications.");
    }
  };

  const markAllServerNotificationsRead = async () => {
    if (!currentUser) {
      return;
    }

    setServerNotificationsStatus("Marking notifications as read...");
    try {
      await request("/api/v1/notifications/read-all", { method: "POST" });
      await loadServerNotifications();
    } catch (error) {
      setServerNotificationsStatus(error.message || "Could not mark notifications as read.");
    }
  };

  const loadProfileCompleteness = async () => {
    if (!currentUser) {
      setProfileCompleteness(null);
      setProfileCompletenessStatus("");
      return;
    }

    setProfileCompletenessStatus("Checking profile completeness...");
    try {
      const data = await request("/api/v1/users/profile/completeness");
      setProfileCompleteness(data);
      setProfileCompletenessStatus("");
    } catch (error) {
      setProfileCompleteness(null);
      setProfileCompletenessStatus(error.message || "Could not check profile completeness.");
    }
  };

  useEffect(() => {
    if (activeView === "notifications") {
      loadAuditLog();
      loadModerationQueue();
      loadServerNotifications();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeView, currentUser?.role]);

  useEffect(() => {
    if (activeView === "profile") {
      loadProfileCompleteness();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeView, currentUser?.userId]);

  const loadMessages = async (groupId) => {
    if (!groupId) {
      setMessages([]);
      setMessageStatus("Select a study group to open chat.");
      return;
    }

    try {
      const data = await request(`/api/v1/study-groups/${groupId}/messages?page=0&size=50`);
      setMessages(data?.messages || []);
      setMessageStatus("Group chat synced.");
    } catch (error) {
      setMessages([]);
      setMessageStatus(error.message || "Could not load group chat.");
    }
  };

  const loadWorkspace = async (user = currentUser) => {
    if (!user) {
      return;
    }

    setWorkspaceNotice({
      type: "loading",
      message: "Syncing your campus workspace...",
    });

    try {
      const [feedData, eventData, groupData, quizData, leaderboardData, puzzleLeaderboardData] = await Promise.all([
        request("/api/v1/feed?page=0&size=20"),
        request("/api/v1/events"),
        request("/api/v1/study-groups"),
        request("/api/v1/game/daily-quiz").catch(() => null),
        request("/api/v1/game/leaderboard?limit=5").catch(() => []),
        request("/api/v1/game/path-puzzle/leaderboard?limit=5").catch(() => []),
      ]);

      const nextJoinStatuses = {};
      const nextCreatorRequests = {};

      await Promise.all((groupData || []).map(async (group) => {
        if (group.creatorId === user.userId) {
          const requests = await request(`/api/v1/study-groups/${group.id}/join-requests`);
          nextCreatorRequests[group.id] = (requests || []).filter(
            (requestItem) => requestItem.status === "PENDING",
          );
          return;
        }

        const myRequest = await request(`/api/v1/study-groups/${group.id}/my-request`, {
          allowNotFound: true,
        });

        if (myRequest?.status) {
          nextJoinStatuses[group.id] = myRequest.status;
        }
      }));

      setFeedPosts(feedData?.posts || []);
      setEvents(Array.isArray(eventData) ? eventData : []);
      setStudyGroups(Array.isArray(groupData) ? groupData : []);
      setJoinStatuses(nextJoinStatuses);
      setCreatorRequests(nextCreatorRequests);
      setDailyQuiz({
        quizDate: quizData?.quizDate || null,
        questions: Array.isArray(quizData?.questions) ? quizData.questions : [],
      });
      setQuizLeaderboard(Array.isArray(leaderboardData) ? leaderboardData : []);
      setPathPuzzleLeaderboard(Array.isArray(puzzleLeaderboardData) ? puzzleLeaderboardData : []);
      setQuizAnswers({});
      setQuizQuestionIndex(0);
      setQuizResult(null);
      setQuizStatus({
        type: "idle",
        message: Array.isArray(quizData?.questions) && quizData.questions.length > 0
          ? "Play the daily quiz to practice OOP and system design fundamentals."
          : "The quiz will appear here when game data is available.",
      });

      const nextAccessibleGroups = (groupData || []).filter((group) =>
        canOpenGroupChat(group, user, nextJoinStatuses));
      const nextSelectedGroupId = nextAccessibleGroups.some(
        (group) => group.id === selectedGroupId,
      )
        ? selectedGroupId
        : nextAccessibleGroups[0]?.id || null;

      setSelectedGroupId(nextSelectedGroupId);

      if (nextSelectedGroupId) {
        await loadMessages(nextSelectedGroupId);
      } else {
        setMessages([]);
        setMessageStatus("Join or create a study group to unlock group chat.");
      }

      const contentCount = (feedData?.posts?.length || 0)
        + (eventData?.length || 0)
        + (groupData?.length || 0);

      setWorkspaceNotice({
        type: "success",
        message: contentCount > 0
          ? "Live campus data loaded from the backend."
          : "Backend connected. Add the first post, event, or study group to start populating the app.",
      });
    } catch (error) {
      setWorkspaceNotice({
        type: "error",
        message: error.message || "The workspace could not be loaded.",
      });
    }
  };

  useEffect(() => {
    const storedToken = window.localStorage.getItem("campusflowToken");
    const storedUser = window.localStorage.getItem("campusflowUser");

    if (!storedToken || !storedUser) {
      return;
    }

    try {
      const parsedUser = JSON.parse(storedUser);
      setCurrentUser(parsedUser);
      setBackendProfileForm({
        username: parsedUser.username || "",
      });
      setActiveView("dashboard");
    } catch (_error) {
      window.localStorage.removeItem("campusflowToken");
      window.localStorage.removeItem("campusflowUser");
    }
  }, []);

  useEffect(() => {
    if (!currentUser) {
      setProfilePreferences(emptyPreferenceProfile);
      setPostInteractions({});
      setSharedResources({});
      return;
    }

    const profileKey = getStorageKey("campusflow-profile", currentUser.email);
    const interactionsKey = getStorageKey("campusflow-post-interactions", currentUser.email);
    const resourceKey = getStorageKey("campusflow-group-resources", currentUser.email);

    setBackendProfileForm({
      username: currentUser.username,
    });
    setProfilePreferences({
      ...emptyPreferenceProfile,
      ...readStoredJson(profileKey, emptyPreferenceProfile),
    });
    setPostInteractions(readStoredJson(interactionsKey, {}));
    setSharedResources(readStoredJson(resourceKey, {}));
    setActiveView("dashboard");

    // eslint-disable-next-line react-hooks/exhaustive-deps
    loadWorkspace(currentUser);
  }, [currentUser]);

  useEffect(() => {
    document.body.dataset.theme = profilePreferences.theme || "light";
  }, [profilePreferences.theme]);

  useEffect(() => {
    if (!currentUser) {
      return;
    }

    window.localStorage.setItem(
      getStorageKey("campusflow-profile", currentUser.email),
      JSON.stringify(profilePreferences),
    );
  }, [currentUser, profilePreferences]);

  useEffect(() => {
    if (!currentUser) {
      return;
    }

    window.localStorage.setItem(
      getStorageKey("campusflow-post-interactions", currentUser.email),
      JSON.stringify(postInteractions),
    );
  }, [currentUser, postInteractions]);

  useEffect(() => {
    if (!currentUser) {
      return;
    }

    window.localStorage.setItem(
      getStorageKey("campusflow-group-resources", currentUser.email),
      JSON.stringify(sharedResources),
    );
  }, [currentUser, sharedResources]);

  useEffect(() => {
    setPuzzlePath([]);
    setPuzzleStarted(false);
    setPuzzleReadyToSubmit(false);
    setPuzzleElapsedSeconds(0);
    setPuzzleStartedAt(null);
    setPuzzleHintsUsed(0);
    setPuzzleHintCellKey(null);
    setPathPuzzleResult(null);
    setPuzzleShareStatus("");
    setShowPuzzleSolution(false);
    setPuzzleStatus({
      type: "idle",
      message: "Choose a route and press Start to fill the full 4x4 grid.",
    });
  }, [selectedPuzzleLevelId]);

  useEffect(() => {
    if (!puzzleStarted || puzzleReadyToSubmit || !puzzleStartedAt) {
      return undefined;
    }

    const intervalId = window.setInterval(() => {
      setPuzzleElapsedSeconds(Math.floor((Date.now() - puzzleStartedAt) / 1000));
    }, 1000);

    return () => window.clearInterval(intervalId);
  }, [puzzleReadyToSubmit, puzzleStarted, puzzleStartedAt]);

  const handleAuthChange = (event) => {
    const { name, value } = event.target;
    setAuthForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleResetChange = (event) => {
    const { name, value } = event.target;
    setResetForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleFormChange = (setter) => (event) => {
    const { name, value, type, checked } = event.target;
    setter((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleOtpAction = async (mode) => {
    const endpoint = mode === "verify"
      ? "/api/v1/users/verify-email"
      : "/api/v1/users/resend-otp";
    const payload = mode === "verify"
      ? {
          email: authForm.email.trim(),
          otpCode: authForm.otpCode.trim(),
        }
      : {
          email: authForm.email.trim(),
        };

    setLoadingAction(mode);
    setStatus({
      type: "loading",
      message: mode === "verify"
        ? "Verifying email with the backend..."
        : "Requesting a fresh OTP...",
    });

    try {
      const data = await request(endpoint, {
        method: "POST",
        body: payload,
        auth: false,
      });

      if (mode === "verify") {
        setAuthPage("login");
        setAuthForm((current) => ({
          ...current,
          otpCode: "",
        }));
      }

      setStatus({
        type: "success",
        message: data?.message
          || (mode === "verify"
            ? "Email verified successfully."
            : "OTP sent successfully."),
      });
    } catch (error) {
      setStatus({
        type: "error",
        message: error.message || "The OTP request failed.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleAuthAction = async (mode) => {
    const endpoint = mode === "register"
      ? "/api/v1/users/register"
      : "/api/v1/users/login";
    const payload = mode === "register"
      ? {
          username: authForm.username.trim(),
          email: authForm.email.trim(),
          password: authForm.password,
        }
      : {
          email: authForm.email.trim(),
          password: authForm.password,
        };

    setLoadingAction(mode);
    setStatus({
      type: "loading",
      message: mode === "register"
        ? "Creating your account..."
        : "Logging you in...",
    });

    try {
      const data = await request(endpoint, {
        method: "POST",
        body: payload,
        auth: false,
      });

      if (mode === "register") {
        setAuthPage("verify");
        setStatus({
          type: "success",
          message: `Registered ${data?.email || authForm.email}. Enter the OTP on the verification page to activate your account.`,
        });
        return;
      }

      const nextUser = {
        userId: data?.userId,
        username: data?.username || authForm.username || "CampusFlow user",
        email: data?.email || authForm.email,
        role: data?.role || "STUDENT",
      };

      window.localStorage.setItem("campusflowToken", data?.token || "");
      updateStoredUser(nextUser);
      setStatus({
        type: "success",
        message: `Logged in as ${nextUser.username}.`,
      });
      setAuthForm((current) => ({
        ...current,
        password: "",
        otpCode: "",
      }));
      setActiveView("dashboard");
    } catch (error) {
      setStatus({
        type: "error",
        message: error.message || "Authentication failed.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleLogout = () => {
    window.localStorage.removeItem("campusflowToken");
    window.localStorage.removeItem("campusflowUser");
    setCurrentUser(null);
    setFeedPosts([]);
    setEvents([]);
    setStudyGroups([]);
    setJoinStatuses({});
    setCreatorRequests({});
    setDailyQuiz({
      quizDate: null,
      questions: [],
    });
    setQuizLeaderboard([]);
    setQuizAnswers({});
    setQuizQuestionIndex(0);
    setQuizResult(null);
    setPathPuzzleLeaderboard([]);
    setPuzzlePath([]);
    setPuzzleStarted(false);
    setPuzzleReadyToSubmit(false);
    setPuzzleElapsedSeconds(0);
    setPuzzleStartedAt(null);
    setPuzzleHintsUsed(0);
    setPuzzleHintCellKey(null);
    setPathPuzzleResult(null);
    setPuzzleShareStatus("");
    setPuzzleStatus({
      type: "idle",
      message: "Choose a route and press Start to begin the path puzzle challenge.",
    });
    setQuizStatus({
      type: "idle",
      message: "Answer five quick questions to earn points and climb the leaderboard.",
    });
    setSelectedGroupId(null);
    setMessages([]);
    setMessageForm(emptyMessageForm);
    setRsvpStateByEvent({});
    setActiveView("auth");
    setAuthPage("login");
    setResetStep("request");
    setResetForm(emptyResetForm);
    setWorkspaceNotice({
      type: "idle",
      message: "You are signed out.",
    });
  };

  const handleFillDemoAccount = (account) => {
    setAuthForm((current) => ({
      ...current,
      username: account.label.replace(" demo", ""),
      email: account.email,
      password: account.password,
      otpCode: "",
    }));
    setStatus({
      type: "idle",
      message: `Filled ${account.label.toLowerCase()} credentials for local testing.`,
    });
    setActiveView("auth");
    setAuthPage("login");
    setResetStep("request");
    setResetForm(emptyResetForm);
  };

  const handleForgotPasswordRequest = async () => {
    const nextEmail = resetForm.email.trim();

    if (!nextEmail) {
      setStatus({
        type: "error",
        message: "Enter your email first.",
      });
      return;
    }

    setLoadingAction("forgot-password");
    setStatus({
      type: "loading",
      message: "Sending a password reset code...",
    });

    try {
      const data = await request("/api/v1/users/forgot-password", {
        method: "POST",
        body: {
          email: nextEmail,
        },
        auth: false,
      });
      setResetStep("confirm");
      setStatus({
        type: "success",
        message: data?.message
          || "If the account exists, a password reset code has been sent.",
      });
    } catch (error) {
      setStatus({
        type: "error",
        message: error.message || "Could not send the password reset code.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleResetPassword = async () => {
    const nextEmail = resetForm.email.trim();
    const nextOtpCode = resetForm.otpCode.trim();

    if (!nextEmail || !nextOtpCode || !resetForm.newPassword || !resetForm.confirmPassword) {
      setStatus({
        type: "error",
        message: "Complete the email, code, and new password fields.",
      });
      return;
    }

    if (resetForm.newPassword !== resetForm.confirmPassword) {
      setStatus({
        type: "error",
        message: "The new passwords do not match.",
      });
      return;
    }

    setLoadingAction("reset-password");
    setStatus({
      type: "loading",
      message: "Updating your password...",
    });

    try {
      const data = await request("/api/v1/users/reset-password", {
        method: "POST",
        body: {
          email: nextEmail,
          otpCode: nextOtpCode,
          newPassword: resetForm.newPassword,
        },
        auth: false,
      });
      setAuthForm((current) => ({
        ...current,
        email: nextEmail,
        password: "",
      }));
      setResetForm(emptyResetForm);
      setResetStep("request");
      setAuthPage("login");
      setStatus({
        type: "success",
        message: data?.message || "Password reset successfully. You can log in now.",
      });
    } catch (error) {
      setStatus({
        type: "error",
        message: error.message || "Could not reset the password.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleSaveProfile = async () => {
    setLoadingAction("profile");
    setWorkspaceNotice({
      type: "loading",
      message: "Updating profile...",
    });

    try {
      const major = profilePreferences.major.trim();
      const interests = profilePreferences.interests.trim();
      const profilePhotoUrl = profilePreferences.profilePhotoUrl.trim();
      const visibility = profilePreferences.privacy === "Public"
        ? "PUBLIC"
        : profilePreferences.privacy === "Private"
          ? "PRIVATE"
          : "CAMPUS_ONLY";

      const data = await request("/api/v1/users/profile", {
        method: "PUT",
        body: {
          username: backendProfileForm.username.trim(),
          major: major ? major : null,
          interests: interests ? interests : null,
          profilePhotoUrl: profilePhotoUrl ? profilePhotoUrl : null,
          visibility,
        },
      });
      const nextUser = {
        ...currentUser,
        username: data?.username || backendProfileForm.username.trim(),
        major: data?.major ?? null,
        interests: data?.interests ?? null,
        profilePhotoUrl: data?.profilePhotoUrl ?? null,
        visibility: data?.visibility ?? null,
      };
      updateStoredUser(nextUser);
      setProfilePreferences((current) => ({
        ...current,
        major: nextUser.major || "",
        interests: nextUser.interests || "",
        profilePhotoUrl: nextUser.profilePhotoUrl || "",
        privacy: nextUser.visibility === "PUBLIC"
          ? "Public"
          : nextUser.visibility === "PRIVATE"
            ? "Private"
            : "Campus only",
      }));
      setWorkspaceNotice({
        type: "success",
        message: "Profile and preferences updated.",
      });
    } catch (error) {
      setWorkspaceNotice({
        type: "error",
        message: error.message || "Profile update failed.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  useEffect(() => {
    return () => {
      if (profilePhotoPreviewUrl) {
        URL.revokeObjectURL(profilePhotoPreviewUrl);
      }
    };
  }, [profilePhotoPreviewUrl]);

  const handleProfilePhotoSelected = (event) => {
    const file = event.target.files && event.target.files[0] ? event.target.files[0] : null;

    if (profilePhotoPreviewUrl) {
      URL.revokeObjectURL(profilePhotoPreviewUrl);
    }

    setProfilePhotoFile(file);
    setProfilePhotoUploadStatus("");

    if (!file) {
      setProfilePhotoPreviewUrl("");
      return;
    }

    setProfilePhotoPreviewUrl(URL.createObjectURL(file));
  };

  const handleUploadProfilePhoto = async () => {
    if (!profilePhotoFile) {
      setProfilePhotoUploadStatus("Choose an image first.");
      return;
    }

    setLoadingAction("profile-photo");
    setProfilePhotoUploadStatus("Uploading...");

    try {
      const token = window.localStorage.getItem("campusflowToken");
      if (!token) {
        throw new Error("You are not logged in.");
      }

      const formData = new FormData();
      formData.append("file", profilePhotoFile);

      const response = await fetch(`${API_BASE_URL}/api/v1/users/profile/photo`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });

      const rawText = await response.text();
      let data = null;
      if (rawText) {
        try {
          data = JSON.parse(rawText);
        } catch (_error) {
          data = rawText;
        }
      }

      if (!response.ok) {
        throw new Error(
          data?.message
            || data?.error
            || (typeof data === "string" ? data : "")
            || `Upload failed with status ${response.status}.`,
        );
      }

      const nextUser = {
        ...currentUser,
        profilePhotoUrl: data?.profilePhotoUrl ?? null,
      };
      updateStoredUser(nextUser);
      setProfilePreferences((current) => ({
        ...current,
        profilePhotoUrl: nextUser.profilePhotoUrl || "",
      }));

      setProfilePhotoUploadStatus("Uploaded.");
      setProfilePhotoFile(null);
      setProfilePhotoPreviewUrl("");
    } catch (error) {
      setProfilePhotoUploadStatus(error.message || "Upload failed.");
    } finally {
      setLoadingAction(null);
    }
  };

  const handleCreatePost = async () => {
    setLoadingAction("post");
    setWorkspaceNotice({
      type: "loading",
      message: "Publishing feed post...",
    });

    try {
      await request("/api/v1/feed", {
        method: "POST",
        body: {
          title: postForm.title.trim(),
          content: postForm.content.trim(),
          type: postForm.type,
        },
      });
      setPostForm(emptyPostForm);
      await loadWorkspace(currentUser);
      setWorkspaceNotice({
        type: "success",
        message: "Feed post created.",
      });
    } catch (error) {
      setWorkspaceNotice({
        type: "error",
        message: error.message || "Could not create the post.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleCreateEvent = async () => {
    setLoadingAction("event");
    setWorkspaceNotice({
      type: "loading",
      message: "Creating event...",
    });

    try {
      await request("/api/v1/events", {
        method: "POST",
        body: {
          title: eventForm.title.trim(),
          description: eventForm.description.trim(),
          eventDate: normalizeDateTime(eventForm.eventDate),
          location: eventForm.location.trim(),
          capacity: Number(eventForm.capacity),
        },
      });
      setEventForm(emptyEventForm);
      await loadWorkspace(currentUser);
      setWorkspaceNotice({
        type: "success",
        message: "Event created.",
      });
    } catch (error) {
      setWorkspaceNotice({
        type: "error",
        message: error.message || "Could not create the event.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleCreateStudyGroup = async () => {
    setLoadingAction("study-group");
    setWorkspaceNotice({
      type: "loading",
      message: "Creating study group...",
    });

    try {
      await request("/api/v1/study-groups", {
        method: "POST",
        body: {
          name: studyGroupForm.name.trim(),
          topic: studyGroupForm.topic.trim(),
          course: studyGroupForm.course.trim(),
          capacity: Number(studyGroupForm.capacity),
        },
      });
      setStudyGroupForm(emptyStudyGroupForm);
      await loadWorkspace(currentUser);
      setWorkspaceNotice({
        type: "success",
        message: "Study group created.",
      });
    } catch (error) {
      setWorkspaceNotice({
        type: "error",
        message: error.message || "Could not create the study group.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleJoinStudyGroup = async (groupId) => {
    setLoadingAction(`join-${groupId}`);
    setWorkspaceNotice({
      type: "loading",
      message: "Sending join request...",
    });

    try {
      const data = await request(`/api/v1/study-groups/${groupId}/join`, {
        method: "POST",
      });
      setJoinStatuses((current) => ({
        ...current,
        [groupId]: data?.status || "PENDING",
      }));
      setWorkspaceNotice({
        type: "success",
        message: "Join request submitted.",
      });
    } catch (error) {
      setWorkspaceNotice({
        type: "error",
        message: error.message || "Could not join the study group.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleJoinRequestDecision = async (requestId, decision) => {
    setLoadingAction(`${decision}-${requestId}`);
    setWorkspaceNotice({
      type: "loading",
      message: decision === "approve"
        ? "Approving join request..."
        : "Rejecting join request...",
    });

    try {
      await request(`/api/v1/study-groups/join-requests/${requestId}/${decision}`, {
        method: "PUT",
      });
      await loadWorkspace(currentUser);
      setWorkspaceNotice({
        type: "success",
        message: decision === "approve"
          ? "Join request approved."
          : "Join request rejected.",
      });
    } catch (error) {
      setWorkspaceNotice({
        type: "error",
        message: error.message || "Could not update the join request.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleToggleRsvp = async (event) => {
    const hasLocalRsvp = Boolean(rsvpStateByEvent[event.id]);

    setLoadingAction(`rsvp-${event.id}`);
    setWorkspaceNotice({
      type: "loading",
      message: hasLocalRsvp ? "Cancelling RSVP..." : "Submitting RSVP...",
    });

    try {
      await request(`/api/v1/events/${event.id}/rsvp`, {
        method: hasLocalRsvp ? "DELETE" : "POST",
      });
      setRsvpStateByEvent((current) => ({
        ...current,
        [event.id]: !hasLocalRsvp,
      }));
      setEvents((current) => current.map((item) => {
        if (item.id !== event.id) {
          return item;
        }
        return {
          ...item,
          rsvpCount: hasLocalRsvp
            ? Math.max(0, item.rsvpCount - 1)
            : item.rsvpCount + 1,
        };
      }));
      setWorkspaceNotice({
        type: "success",
        message: hasLocalRsvp ? "RSVP cancelled." : "RSVP confirmed.",
      });
    } catch (error) {
      setWorkspaceNotice({
        type: "error",
        message: error.message || "Could not update RSVP.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleReaction = (postId, reaction) => {
    setPostInteractions((current) => {
      const existing = current[postId] || {};
      return {
        ...current,
        [postId]: {
          ...existing,
          reaction: existing.reaction === reaction ? null : reaction,
        },
      };
    });
  };

  const handleCommentDraftChange = (postId, value) => {
    setPostInteractions((current) => {
      const existing = current[postId] || {};
      return {
        ...current,
        [postId]: {
          ...existing,
          commentDraft: value,
        },
      };
    });
  };

  const handleAddComment = (postId) => {
    setPostInteractions((current) => {
      const existing = current[postId] || {};
      const nextContent = (existing.commentDraft || "").trim();
      if (!nextContent || !currentUser) {
        return current;
      }
      return {
        ...current,
        [postId]: {
          ...existing,
          commentDraft: "",
          comments: [
            ...(existing.comments || []),
            {
              id: `${postId}-${Date.now()}`,
              author: currentUser.username,
              content: nextContent,
              createdAt: new Date().toISOString(),
            },
          ],
        },
      };
    });
  };

  const handleReportPost = (postId) => {
    if (!currentUser) {
      return;
    }

    const existing = postInteractions[postId] || {};
    const nextReported = !existing.reported;

    setPostInteractions((current) => ({
      ...current,
      [postId]: {
        ...(current[postId] || {}),
        reported: nextReported,
        reportedAt: nextReported ? new Date().toISOString() : null,
      },
    }));

    if (!nextReported) {
      return;
    }

    setWorkspaceNotice({
      type: "loading",
      message: "Reporting content...",
    });

    request("/api/v1/moderation/reports", {
      method: "POST",
      body: {
        targetType: "POST",
        targetId: String(postId),
        reason: "USER_REPORT",
        detail: "Reported from the feed UI.",
      },
    }).then(() => {
      setWorkspaceNotice({
        type: "success",
        message: "Report submitted to moderators.",
      });
    }).catch((error) => {
      setPostInteractions((current) => ({
        ...current,
        [postId]: {
          ...(current[postId] || {}),
          reported: false,
          reportedAt: null,
        },
      }));
      setWorkspaceNotice({
        type: "error",
        message: error.message || "Could not submit the report.",
      });
    });
  };

  const handleOpenChat = async (groupId) => {
    setSelectedGroupId(groupId);
    await loadMessages(groupId);
    connectToGroupSocket(groupId);
    setUnreadGroups((prev) => {
      const next = new Set(prev);
      next.delete(groupId);
      return next;
    });

    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      messages.forEach((message) => {
        if (!message?.id) {
          return;
        }
        try {
          socketRef.current.send(JSON.stringify({ type: "read", messageId: message.id }));
        } catch (_e) {
          // ignore
        }
      });
    }
  };

  const handleSendMessage = async () => {
    if (!selectedGroupId) {
      return;
    }

    setLoadingAction("message");
    setMessageStatus("Sending message...");

    try {
      await request(`/api/v1/study-groups/${selectedGroupId}/messages`, {
        method: "POST",
        body: {
          content: messageForm.content.trim(),
        },
      });
      setMessageForm(emptyMessageForm);
      await loadMessages(selectedGroupId);
      setWorkspaceNotice({
        type: "success",
        message: "Message sent.",
      });
    } catch (error) {
      setMessageStatus(error.message || "Could not send the message.");
      setWorkspaceNotice({
        type: "error",
        message: error.message || "Could not send the message.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  useEffect(() => {
    return () => {
      if (chatFilePreviewUrl) {
        URL.revokeObjectURL(chatFilePreviewUrl);
      }
    };
  }, [chatFilePreviewUrl]);

  const handleChatFileSelected = (event) => {
    const file = event.target.files && event.target.files[0] ? event.target.files[0] : null;

    if (chatFilePreviewUrl) {
      URL.revokeObjectURL(chatFilePreviewUrl);
    }

    setChatFile(file);
    setChatFileStatus("");

    if (!file) {
      setChatFilePreviewUrl("");
      return;
    }

    if (file.type && file.type.startsWith("image/")) {
      setChatFilePreviewUrl(URL.createObjectURL(file));
    } else {
      setChatFilePreviewUrl("");
    }
  };

  const handleSendChatFile = async () => {
    if (!selectedGroupId) {
      return;
    }
    if (!chatFile) {
      setChatFileStatus("Choose a file first.");
      return;
    }

    setLoadingAction("message-file");
    setChatFileStatus("Uploading...");

    try {
      const token = window.localStorage.getItem("campusflowToken");
      if (!token) {
        throw new Error("You are not logged in.");
      }

      const formData = new FormData();
      formData.append("file", chatFile);

      const response = await fetch(
        `${API_BASE_URL}/api/v1/study-groups/${selectedGroupId}/messages/file`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
          body: formData,
        },
      );

      const rawText = await response.text();
      let data = null;
      if (rawText) {
        try {
          data = JSON.parse(rawText);
        } catch (_error) {
          data = rawText;
        }
      }

      if (!response.ok) {
        throw new Error(
          data?.message
            || data?.error
            || (typeof data === "string" ? data : "")
            || `Upload failed with status ${response.status}.`,
        );
      }

      setChatFileStatus("Sent.");
      setChatFile(null);
      if (chatFilePreviewUrl) {
        URL.revokeObjectURL(chatFilePreviewUrl);
      }
      setChatFilePreviewUrl("");
      await loadMessages(selectedGroupId);
    } catch (error) {
      setChatFileStatus(error.message || "Could not send the file.");
    } finally {
      setLoadingAction(null);
    }
  };

  const handleAddResource = () => {
    if (!selectedGroupId || !currentUser) {
      return;
    }

    const nextTitle = resourceForm.title.trim();
    const nextUrl = resourceForm.url.trim();

    if (!nextTitle || !nextUrl) {
      return;
    }

    setSharedResources((current) => ({
      ...current,
      [selectedGroupId]: [
        ...(current[selectedGroupId] || []),
        {
          id: `${selectedGroupId}-${Date.now()}`,
          title: nextTitle,
          url: nextUrl,
          addedBy: currentUser.username,
          createdAt: new Date().toISOString(),
        },
      ],
    }));
    setResourceForm(emptyResourceForm);
  };

  const handleQuizOptionSelect = (questionId, optionIndex) => {
    setQuizAnswers((current) => ({
      ...current,
      [questionId]: optionIndex,
    }));
  };

  const handleQuizRestart = () => {
    setQuizAnswers({});
    setQuizQuestionIndex(0);
    setQuizResult(null);
    setQuizStatus({
      type: "idle",
      message: "The quiz is reset. Answer all five questions when you are ready.",
    });
  };

  const handleSubmitQuiz = async () => {
    if (!dailyQuiz.questions.length) {
      return;
    }

    const firstUnansweredQuestion = dailyQuiz.questions.find(
      (question) => quizAnswers[question.id] === undefined,
    );

    if (firstUnansweredQuestion) {
      setQuizQuestionIndex(dailyQuiz.questions.findIndex(
        (question) => question.id === firstUnansweredQuestion.id,
      ));
      setQuizStatus({
        type: "error",
        message: "Answer every question before submitting your quiz.",
      });
      return;
    }

    setLoadingAction("quiz-submit");
    setQuizStatus({
      type: "loading",
      message: "Submitting your quiz score...",
    });

    try {
      const submission = await request("/api/v1/game/daily-quiz/submit", {
        method: "POST",
        body: {
          answers: dailyQuiz.questions.map((question) => ({
            questionId: question.id,
            selectedOptionIndex: quizAnswers[question.id],
          })),
        },
      });
      const leaderboard = await request("/api/v1/game/leaderboard?limit=5");

      setQuizResult(submission);
      setQuizLeaderboard(Array.isArray(leaderboard) ? leaderboard : []);
      setQuizStatus({
        type: "success",
        message: `You scored ${submission?.score || 0} points and now have ${submission?.totalPoints || 0} total points.`,
      });
    } catch (error) {
      setQuizStatus({
        type: "error",
        message: error.message || "The quiz could not be submitted.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleStartPathPuzzle = () => {
    const startCell = currentPuzzleLevel.checkpoints[0];
    setPuzzlePath([{ ...startCell }]);
    setPuzzleStarted(true);
    setPuzzleReadyToSubmit(false);
    setPuzzleElapsedSeconds(0);
    setPuzzleStartedAt(Date.now());
    setPuzzleHintsUsed(0);
    setPuzzleHintCellKey(null);
    setPathPuzzleResult(null);
    setPuzzleShareStatus("");
    setShowPuzzleSolution(false);
    setPuzzleStatus({
      type: "idle",
      message: "Move one cell at a time, visit the numbered points in order, and fill the whole 4x4 grid.",
    });
  };

  const handleResetPathPuzzle = () => {
    setPuzzlePath([]);
    setPuzzleStarted(false);
    setPuzzleReadyToSubmit(false);
    setPuzzleElapsedSeconds(0);
    setPuzzleStartedAt(null);
    setPuzzleHintsUsed(0);
    setPuzzleHintCellKey(null);
    setPathPuzzleResult(null);
    setPuzzleShareStatus("");
    setShowPuzzleSolution(false);
    setPuzzleStatus({
      type: "idle",
      message: "Puzzle reset. Press Start whenever you want a fresh attempt.",
    });
  };

  const handleUndoPathPuzzleMove = () => {
    if (!puzzleStarted) {
      setPuzzleStatus({
        type: "error",
        message: "Start the level before using undo.",
      });
      return;
    }

    if (puzzlePath.length <= 1) {
      setPuzzleStatus({
        type: "error",
        message: "You are already back at the starting point.",
      });
      return;
    }

    setPuzzlePath((current) => current.slice(0, -1));
    setPuzzleReadyToSubmit(false);
    setPuzzleHintCellKey(null);
    setPathPuzzleResult(null);
    setPuzzleShareStatus("");
    setShowPuzzleSolution(false);
    setPuzzleStatus({
      type: "idle",
      message: "Last move removed. Keep tracing the route.",
    });
  };

  const handlePathPuzzleHint = () => {
    if (!puzzleStarted) {
      setPuzzleStatus({
        type: "error",
        message: "Start the level first to unlock hints.",
      });
      return;
    }

    if (puzzleReadyToSubmit) {
      setPuzzleStatus({
        type: "idle",
        message: "Your grid is full. Submit it to save the score and reveal the reference solution.",
      });
      return;
    }

    const nextCheckpoint = getNextRequiredCheckpoint(
      currentPuzzleLevel,
      puzzlePath,
      currentPuzzleCheckpointMap,
    );

    if (!nextCheckpoint) {
      setPuzzleHintCellKey(null);
      setPuzzleStatus({
        type: "idle",
        message: remainingPuzzleCells > 0
          ? `All numbered points are connected. Fill ${remainingPuzzleCells} more ${remainingPuzzleCells === 1 ? "cell" : "cells"} to finish the board.`
          : "The board is complete. Submit it whenever you are ready.",
      });
      return;
    }

    setPuzzleHintsUsed((current) => current + 1);
    setPuzzleHintCellKey(coordsKey(nextCheckpoint.x, nextCheckpoint.y));
    setPuzzleStatus({
      type: "idle",
      message: `Head toward point ${nextCheckpoint.label} next while keeping room to cover the rest of the grid.`,
    });
  };

  const handlePathPuzzleCellClick = (x, y) => {
    if (!puzzleStarted) {
      setPuzzleStatus({
        type: "error",
        message: "Press Start before drawing your route.",
      });
      return;
    }

    if (puzzleReadyToSubmit) {
      setPuzzleStatus({
        type: "idle",
        message: "The route is finished. Submit it or reset to try again.",
      });
      return;
    }

    const nextCell = { x, y };
    const nextCellKey = coordsKey(x, y);
    const lastCell = puzzlePath[puzzlePath.length - 1];

    if (puzzlePathKeys.has(nextCellKey)) {
      setPuzzleStatus({
        type: "error",
        message: "That cell is already part of your route. Use Undo if you want to backtrack.",
      });
      return;
    }

    if (!isAdjacentCell(lastCell, nextCell)) {
      setPuzzleStatus({
        type: "error",
        message: "Move to a neighboring cell to keep the line continuous.",
      });
      return;
    }

    const nextRequiredCheckpoint = getVisitedCheckpointLabels(
      puzzlePath,
      currentPuzzleCheckpointMap,
    ).length + 1;
    const clickedCheckpoint = currentPuzzleCheckpointMap.get(nextCellKey);

    if (clickedCheckpoint && clickedCheckpoint !== nextRequiredCheckpoint) {
      setPuzzleStatus({
        type: "error",
        message: `Follow the numbered order. Point ${nextRequiredCheckpoint} should come next.`,
      });
      return;
    }

    const nextPath = [...puzzlePath, nextCell];
    setPuzzlePath(nextPath);
    setPuzzleHintCellKey(null);
    setPathPuzzleResult(null);
    setPuzzleShareStatus("");
    setShowPuzzleSolution(false);

    if (isPuzzleGridFilled(currentPuzzleLevel, nextPath)) {
      setPuzzleReadyToSubmit(true);
      setPuzzleStatus({
        type: "success",
        message: "Nice. The full 4x4 grid is connected. Submit to score it and view the reference solution.",
      });
      return;
    }

    setPuzzleStatus({
      type: clickedCheckpoint === currentPuzzleLevel.checkpoints.length ? "success" : "idle",
      message: clickedCheckpoint
        ? clickedCheckpoint === currentPuzzleLevel.checkpoints.length
          ? `All numbered points are connected. Fill ${puzzleTotalCells - nextPath.length} more ${(puzzleTotalCells - nextPath.length) === 1 ? "cell" : "cells"} to complete the board.`
          : `Great. Point ${clickedCheckpoint} is connected. Keep filling the grid.`
        : "Route extended. Keep connecting the points in order.",
    });
  };

  const handleSubmitPathPuzzle = async () => {
    if (!puzzleReadyToSubmit) {
      setPuzzleStatus({
        type: "error",
        message: "Fill the entire 4x4 route before submitting your score.",
      });
      return;
    }

    const isValidRoute = isPuzzleGridFilled(currentPuzzleLevel, puzzlePath)
      && didVisitCheckpointsInOrder(
        currentPuzzleLevel,
        puzzlePath,
        currentPuzzleCheckpointMap,
      );

    setShowPuzzleSolution(true);

    if (!isValidRoute) {
      setPuzzleStatus({
        type: "error",
        message: "This attempt does not satisfy the puzzle rules. A reference solution is shown below.",
      });
      return;
    }

    const durationSeconds = puzzleElapsedSeconds;
    const score = calculatePathPuzzleScore(
      currentPuzzleLevel,
      durationSeconds,
      puzzleMovesUsed,
      puzzleHintsUsed,
    );

    setLoadingAction("path-puzzle-submit");
    setPuzzleStatus({
      type: "loading",
      message: "Saving your puzzle score...",
    });

    try {
      const submission = await request("/api/v1/game/path-puzzle/submit", {
        method: "POST",
        body: {
          levelId: currentPuzzleLevel.id,
          levelName: currentPuzzleLevel.name,
          score,
          durationSeconds,
          movesUsed: puzzleMovesUsed,
          hintsUsed: puzzleHintsUsed,
        },
      });
      const leaderboard = await request("/api/v1/game/path-puzzle/leaderboard?limit=5");

      setPathPuzzleResult(submission);
      setPathPuzzleLeaderboard(Array.isArray(leaderboard) ? leaderboard : []);
      setPuzzleShareStatus("");
      setShowPuzzleSolution(true);
      setPuzzleStatus({
        type: "success",
        message: `Route solved. You earned ${submission?.score || score} points in ${formatDuration(durationSeconds)}. The reference solution is shown below.`,
      });
    } catch (error) {
      setPuzzleStatus({
        type: "error",
        message: error.message || "The path puzzle score could not be saved.",
      });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleSharePathPuzzleResult = async () => {
    if (!pathPuzzleResult || !currentUser) {
      return;
    }

    const shareText = `${currentUser.username} solved ${pathPuzzleResult.levelName} on CampusFlow in ${formatDuration(pathPuzzleResult.durationSeconds)} for ${pathPuzzleResult.score} points.`;

    try {
      if (window.navigator?.clipboard?.writeText) {
        await window.navigator.clipboard.writeText(shareText);
        setPuzzleShareStatus("Copied a share-ready result to your clipboard.");
      } else {
        setPuzzleShareStatus(shareText);
      }
    } catch (_error) {
      setPuzzleShareStatus(shareText);
    }
  };

  const renderSummary = () => (
    <div className="summary-grid">
      <button
        type="button"
        className="summary-card summary-rose summary-button"
        onClick={() => setActiveView("feed")}
      >
        <span>Feed</span>
        <strong>{feedPosts.length}</strong>
        <small>Open community posts</small>
      </button>
      <button
        type="button"
        className="summary-card summary-blush summary-button"
        onClick={() => setActiveView("events")}
      >
        <span>Events</span>
        <strong>{events.length}</strong>
        <small>See upcoming plans</small>
      </button>
      <button
        type="button"
        className="summary-card summary-petal summary-button"
        onClick={() => setActiveView("groups")}
      >
        <span>Groups</span>
        <strong>{studyGroups.length}</strong>
        <small>Jump into study spaces</small>
      </button>
      <button
        type="button"
        className="summary-card summary-lilac summary-button"
        onClick={() => setActiveView("profile")}
      >
        <span>Profile</span>
        <strong>{currentUser?.username?.slice(0, 1).toUpperCase() || "U"}</strong>
        <small>Manage your space</small>
      </button>
    </div>
  );

  const renderDashboard = () => (
    <div className="page-stack">
      <section className="dashboard-hero">
        <div>
          <p className="dashboard-kicker">Dashboard</p>
          <h2>Your campus at a glance</h2>
          <p className="card-copy">
            Open the main areas directly from here and keep the dashboard focused on overview, not notifications.
          </p>
        </div>
        <div className="dashboard-hero-card">
          <span className="dashboard-hero-label">Welcome back</span>
          <strong>{currentUser?.username || "CampusFlow user"}</strong>
          <p>{workspaceNotice.message}</p>
          <div className="dashboard-badge-row">
            <span className="panel-chip">Daily quiz live</span>
            <span className="panel-chip">{quizLeaderboard.length} players ranked</span>
          </div>
        </div>
      </section>

      {renderSummary()}

      <div className="dashboard-grid">
        <article className="data-panel dashboard-panel-large">
          <div className="panel-header">
            <h3>Explore CampusFlow</h3>
            <span className="panel-chip">Quick routes</span>
          </div>
          <div className="dashboard-feature-grid">
            <button type="button" className="dashboard-feature-card" onClick={() => setActiveView("feed")}>
              <span className="feature-tag">Feed</span>
              <strong>Community updates</strong>
              <p>Announcements, reactions, and student conversations in one place.</p>
            </button>
            <button type="button" className="dashboard-feature-card" onClick={() => setActiveView("events")}>
              <span className="feature-tag">Events</span>
              <strong>Upcoming campus moments</strong>
              <p>Track what is happening next and jump into RSVPs quickly.</p>
            </button>
            <button type="button" className="dashboard-feature-card" onClick={() => setActiveView("groups")}>
              <span className="feature-tag">Groups</span>
              <strong>Study together</strong>
              <p>Open your active groups, requests, and shared resources.</p>
            </button>
            <button type="button" className="dashboard-feature-card" onClick={() => setActiveView("profile")}>
              <span className="feature-tag">Profile</span>
              <strong>Personalize your account</strong>
              <p>Update your identity, privacy settings, and preferences.</p>
            </button>
          </div>
          <div className="dashboard-quick-strip">
            <button type="button" className="secondary tile-button" onClick={() => setActiveView("feed")}>
              Community feed
            </button>
            <button type="button" className="secondary tile-button" onClick={() => setActiveView("groups")}>
              Study groups
            </button>
            <button type="button" className="secondary tile-button" onClick={() => setActiveView("events")}>
              Event RSVP
            </button>
            <button type="button" className="secondary tile-button" onClick={() => loadWorkspace(currentUser)}>
              Refresh workspace
            </button>
          </div>
        </article>

        <article className="data-panel">
          <div className="panel-header">
            <h3>Daily quiz</h3>
            <span className="panel-chip">{dailyQuiz.quizDate || "Today"}</span>
          </div>
          {currentQuizQuestion ? (
            <>
              <div className="quiz-progress-row">
                <span>Question {quizQuestionIndex + 1} of {dailyQuiz.questions.length}</span>
                <span>{answeredQuizCount}/{dailyQuiz.questions.length} answered</span>
              </div>

              <div className="quiz-question-card">
                <span className="feature-tag">{currentQuizQuestion.category}</span>
                <strong>{currentQuizQuestion.prompt}</strong>
              </div>

              <div className="quiz-options">
                {currentQuizQuestion.options.map((option, optionIndex) => (
                  <button
                    key={option}
                    type="button"
                    className={`quiz-option ${quizAnswers[currentQuizQuestion.id] === optionIndex ? "quiz-option-selected" : ""}`}
                    onClick={() => handleQuizOptionSelect(currentQuizQuestion.id, optionIndex)}
                  >
                    <span className="quiz-option-index">{String.fromCharCode(65 + optionIndex)}</span>
                    <span>{option}</span>
                  </button>
                ))}
              </div>

              <div className="action-inline quiz-action-row">
                <button
                  type="button"
                  className="secondary inline-button"
                  onClick={handleQuizRestart}
                >
                  Reset
                </button>
                <button
                  type="button"
                  className="secondary inline-button"
                  onClick={() => setQuizQuestionIndex((current) => Math.max(0, current - 1))}
                  disabled={quizQuestionIndex === 0}
                >
                  Previous
                </button>
                {quizQuestionIndex < dailyQuiz.questions.length - 1 ? (
                  <button
                    type="button"
                    className="secondary inline-button"
                    onClick={() => setQuizQuestionIndex((current) => Math.min(dailyQuiz.questions.length - 1, current + 1))}
                  >
                    Next
                  </button>
                ) : (
                  <button
                    type="button"
                    onClick={handleSubmitQuiz}
                    disabled={loadingAction === "quiz-submit"}
                  >
                    {loadingAction === "quiz-submit" ? "Submitting..." : "Submit quiz"}
                  </button>
                )}
              </div>

              {quizResult ? (
                <div className="quiz-result-card">
                  <strong>{quizResult.score} / {quizResult.totalQuestions * 20} points</strong>
                  <p>
                    {quizResult.correctAnswers} correct answers, {quizResult.totalPoints} total points,
                    best score {quizResult.bestScore}.
                  </p>
                </div>
              ) : null}

              <div className={`status-banner status-${quizStatus.type}`} role="status">
                {quizStatus.message}
              </div>
            </>
          ) : (
            <p className="empty-copy">The quiz challenge will appear here when it is loaded from the backend.</p>
          )}
        </article>

        <article className="data-panel">
          <div className="panel-header">
            <h3>Leaderboard</h3>
            <span className="panel-chip">Top players</span>
          </div>
          <div className="list-stack">
            {quizLeaderboard.length === 0 ? (
              <p className="empty-copy">Leaderboard data will appear here after scores are recorded.</p>
            ) : (
              quizLeaderboard.map((entry, index) => (
                <div className="list-card leaderboard-card" key={entry.userId}>
                  <div className="leaderboard-rank">#{index + 1}</div>
                  <div className="leaderboard-copy">
                    <strong>{entry.username}</strong>
                    <p>{formatRole(entry.role)} · {entry.totalPoints} total points</p>
                    <small>Best score {entry.bestScore} · {entry.attempts} attempts</small>
                  </div>
                </div>
              ))
            )}
          </div>
        </article>
      </div>

      <div className="dashboard-games-grid">
        <article className="data-panel puzzle-panel">
          <div className="panel-header">
            <div>
              <h3>Path puzzle challenge</h3>
              <p className="card-copy">
                Fill the whole 4x4 grid with one continuous path while visiting the numbered checkpoints in order.
              </p>
            </div>
            <span className="panel-chip">Simpler 4x4 mode</span>
          </div>

          <div className="puzzle-topbar">
            <label className="field field-inline">
              <span>Level</span>
              <select
                value={selectedPuzzleLevelId}
                onChange={(event) => setSelectedPuzzleLevelId(event.target.value)}
              >
                {pathPuzzleLevels.map((level) => (
                  <option key={level.id} value={level.id}>
                    {level.name}
                  </option>
                ))}
              </select>
            </label>

            <div className="puzzle-stats-strip">
              <div className="puzzle-stat-card">
                <span>Time</span>
                <strong>{formatDuration(puzzleElapsedSeconds)}</strong>
              </div>
              <div className="puzzle-stat-card">
                <span>Moves</span>
                <strong>{puzzleMovesUsed}</strong>
              </div>
              <div className="puzzle-stat-card">
                <span>Hints</span>
                <strong>{puzzleHintsUsed}</strong>
              </div>
            </div>
          </div>

          <div className="puzzle-level-card">
            <strong>{currentPuzzleLevel.name}</strong>
            <p>{currentPuzzleLevel.description}</p>
            <small>{currentPuzzleLevel.checkpoints.length} checkpoints · {currentPuzzleLevel.size}x{currentPuzzleLevel.size} grid</small>
          </div>

          <p className="puzzle-helper-copy">
            More than one answer can be correct. The goal is to keep the route connected and fill every tile.
          </p>

          <div className="puzzle-controls">
            <button
              type="button"
              onClick={handleStartPathPuzzle}
            >
              {puzzleStarted ? "Restart level" : "Start"}
            </button>
            <button
              type="button"
              className="secondary"
              onClick={handleUndoPathPuzzleMove}
              disabled={!puzzleStarted || puzzlePath.length <= 1}
            >
              Undo
            </button>
            <button
              type="button"
              className="secondary"
              onClick={handlePathPuzzleHint}
              disabled={!puzzleStarted}
            >
              Hint
            </button>
            <button
              type="button"
              className="secondary"
              onClick={handleResetPathPuzzle}
            >
              Reset
            </button>
            <button
              type="button"
              onClick={handleSubmitPathPuzzle}
              disabled={!puzzleReadyToSubmit || loadingAction === "path-puzzle-submit"}
            >
              {loadingAction === "path-puzzle-submit" ? "Submitting..." : "Submit route"}
            </button>
          </div>

          <div
            className="puzzle-board"
            style={{ gridTemplateColumns: `repeat(${currentPuzzleLevel.size}, minmax(0, 1fr))` }}
          >
            {Array.from({ length: currentPuzzleLevel.size }).flatMap((_, y) =>
              Array.from({ length: currentPuzzleLevel.size }).map((__, x) => {
                const cellKey = coordsKey(x, y);
                const checkpointLabel = currentPuzzleCheckpointMap.get(cellKey);
                const isActive = puzzlePathKeys.has(cellKey);
                const isHinted = puzzleHintCellKey === cellKey;
                const isStartCell = currentPuzzleLevel.checkpoints[0].x === x && currentPuzzleLevel.checkpoints[0].y === y;
                const isFinalCheckpoint = checkpointLabel === currentPuzzleLevel.checkpoints.length;

                return (
                  <button
                    key={cellKey}
                    type="button"
                    className={[
                      "puzzle-cell",
                      checkpointLabel ? "puzzle-cell-checkpoint" : "",
                      isActive ? "puzzle-cell-active" : "",
                      isHinted ? "puzzle-cell-hint" : "",
                      isStartCell ? "puzzle-cell-start" : "",
                      isFinalCheckpoint ? "puzzle-cell-finish" : "",
                    ].filter(Boolean).join(" ")}
                    onClick={() => handlePathPuzzleCellClick(x, y)}
                  >
                    {checkpointLabel ? <span className="puzzle-cell-label">{checkpointLabel}</span> : null}
                  </button>
                );
              }))}
          </div>

          <div className={`status-banner status-${puzzleStatus.type}`} role="status">
            {puzzleStatus.message}
          </div>

          {pathPuzzleResult ? (
            <div className="puzzle-result-card">
              <div>
                <strong>{pathPuzzleResult.score} points</strong>
                <p>
                  {pathPuzzleResult.levelName} solved in {formatDuration(pathPuzzleResult.durationSeconds)}
                  {" "}with {pathPuzzleResult.movesUsed} moves and {pathPuzzleResult.hintsUsed} hints.
                </p>
              </div>
              <button
                type="button"
                className="secondary"
                onClick={handleSharePathPuzzleResult}
              >
                Share result
              </button>
            </div>
          ) : null}

          {puzzleShareStatus ? (
            <div className="puzzle-share-note">
              {puzzleShareStatus}
            </div>
          ) : null}

          {showPuzzleSolution ? (
            <div className="puzzle-solution-card">
              <div className="panel-header">
                <div>
                  <h3>Reference solution</h3>
                  <p className="card-copy">
                    This is one valid answer. Your own route can be different as long as it fills the grid and respects the checkpoint order.
                  </p>
                </div>
              </div>

              <div
                className="puzzle-board puzzle-board-reference"
                style={{ gridTemplateColumns: `repeat(${currentPuzzleLevel.size}, minmax(0, 1fr))` }}
              >
                {Array.from({ length: currentPuzzleLevel.size }).flatMap((_, y) =>
                  Array.from({ length: currentPuzzleLevel.size }).map((__, x) => {
                    const cellKey = coordsKey(x, y);
                    const checkpointLabel = currentPuzzleCheckpointMap.get(cellKey);
                    const solutionStep = currentPuzzleSolutionStepMap.get(cellKey);

                    return (
                      <div
                        key={`solution-${cellKey}`}
                        className={[
                          "puzzle-cell",
                          "puzzle-cell-reference",
                          currentPuzzleSolutionKeys.has(cellKey) ? "puzzle-cell-active" : "",
                          checkpointLabel ? "puzzle-cell-checkpoint" : "",
                        ].filter(Boolean).join(" ")}
                      >
                        <span className="puzzle-cell-step">{solutionStep}</span>
                        {checkpointLabel ? (
                          <small className="puzzle-cell-badge">#{checkpointLabel}</small>
                        ) : null}
                      </div>
                    );
                  }))}
              </div>
            </div>
          ) : null}
        </article>

        <article className="data-panel">
          <div className="panel-header">
            <h3>Path puzzle scoreboard</h3>
            <span className="panel-chip">Fastest clean routes</span>
          </div>

          <div className="list-stack">
            {pathPuzzleLeaderboard.length === 0 ? (
              <p className="empty-copy">Scores will appear here once players begin finishing the puzzle routes.</p>
            ) : (
              pathPuzzleLeaderboard.map((entry, index) => (
                <div className="list-card leaderboard-card" key={`${entry.userId}-${entry.completedAt}`}>
                  <div className="leaderboard-rank">#{index + 1}</div>
                  <div className="leaderboard-copy">
                    <strong>{entry.username}</strong>
                    <p>{entry.levelName} · {entry.score} pts</p>
                    <small>
                      {formatRole(entry.role)} · {formatDuration(entry.durationSeconds)} · {entry.movesUsed} moves · {entry.hintsUsed} hints
                    </small>
                  </div>
                </div>
              ))
            )}
          </div>

          <div className="puzzle-info-card">
            <strong>How scoring works</strong>
            <p>Higher levels score more, while slower times, extra moves, and hints reduce the final total.</p>
            <ul className="notes-list">
              <li>Use Undo to safely backtrack without resetting the whole board.</li>
              <li>Hints highlight the next correct cell if you drift from the intended route.</li>
              <li>Submit only after you connect every checkpoint and reach the final point.</li>
            </ul>
          </div>
        </article>
      </div>
    </div>
  );

  const renderFeed = () => (
    <div className="page-stack">
      <section className="page-header">
        <div>
          <h2>Community feed</h2>
          <p className="card-copy">
            Browse announcements, react, comment, and report content from one place.
          </p>
        </div>
      </section>

      {canCreateModerationContent(currentUser) ? (
        <article className="data-panel">
          <div className="panel-header">
            <h3>Create post</h3>
            <span className="panel-chip">Moderators and admins</span>
          </div>
          <div className="form-grid">
            <label className="field">
              <span>Title</span>
              <input
                name="title"
                value={postForm.title}
                onChange={handleFormChange(setPostForm)}
              />
            </label>
            <label className="field">
              <span>Type</span>
              <select
                name="type"
                value={postForm.type}
                onChange={handleFormChange(setPostForm)}
              >
                <option value="ANNOUNCEMENT">Announcement</option>
                <option value="EVENT_UPDATE">Event update</option>
                <option value="GENERAL">General</option>
              </select>
            </label>
          </div>
          <label className="field">
            <span>Content</span>
            <textarea
              name="content"
              rows="4"
              value={postForm.content}
              onChange={handleFormChange(setPostForm)}
            />
          </label>
          <button
            type="button"
            onClick={handleCreatePost}
            disabled={loadingAction !== null}
          >
            {loadingAction === "post" ? "Publishing..." : "Publish post"}
          </button>
        </article>
      ) : null}

      <article className="data-panel">
        <div className="control-row">
          <input
            placeholder="Search posts"
            value={feedSearch}
            onChange={(event) => setFeedSearch(event.target.value)}
          />
          <select
            value={feedTypeFilter}
            onChange={(event) => setFeedTypeFilter(event.target.value)}
          >
            <option value="ALL">All types</option>
            <option value="ANNOUNCEMENT">Announcements</option>
            <option value="EVENT_UPDATE">Event updates</option>
            <option value="GENERAL">General</option>
          </select>
        </div>

        <div className="list-stack">
          {filteredFeed.length === 0 ? (
            <p className="empty-copy">No posts match the current filter.</p>
          ) : (
            filteredFeed.map((post) => {
              const interaction = postInteractions[post.id] || {};
              const comments = interaction.comments || [];

              return (
                <div className="list-card feed-card" key={post.id}>
                  <div className="list-topline">
                    <strong>{post.title}</strong>
                    <span>{post.type}</span>
                  </div>
                  <p>{post.content}</p>
                  <small>
                    By {post.authorUsername} • {formatDateTime(post.createdAt)}
                  </small>

                  <div className="reaction-row">
                    {reactionChoices.map((reaction) => (
                      <button
                        key={reaction}
                        type="button"
                        className={`secondary inline-button ${interaction.reaction === reaction ? "selected-button" : ""}`}
                        onClick={() => handleReaction(post.id, reaction)}
                      >
                        {reaction}
                      </button>
                    ))}
                    <button
                      type="button"
                      className={`secondary inline-button ${interaction.reported ? "reported-button" : ""}`}
                      onClick={() => handleReportPost(post.id)}
                    >
                      {interaction.reported ? "Reported" : "Report"}
                    </button>
                  </div>

                  <div className="comment-box">
                    <label className="field">
                      <span>Comments</span>
                      <textarea
                        rows="2"
                        placeholder="Write a comment"
                        value={interaction.commentDraft || ""}
                        onChange={(event) => handleCommentDraftChange(post.id, event.target.value)}
                      />
                    </label>
                    <button
                      type="button"
                      className="secondary inline-button"
                      onClick={() => handleAddComment(post.id)}
                    >
                      Add comment
                    </button>
                    {comments.length > 0 ? (
                      <div className="comment-list">
                        {comments.map((comment) => (
                          <div className="comment-item" key={comment.id}>
                            <strong>{comment.author}</strong>
                            <p>{comment.content}</p>
                            <small>{formatDateTime(comment.createdAt)}</small>
                          </div>
                        ))}
                      </div>
                    ) : null}
                  </div>
                </div>
              );
            })
          )}
        </div>
      </article>

      {(currentUser?.role === "MODERATOR" || currentUser?.role === "ADMIN") ? (
        <article className="data-panel">
          <div className="panel-header">
            <h3>Moderation audit log</h3>
            <button
              type="button"
              className="secondary inline-button"
              onClick={loadAuditLog}
            >
              Refresh
            </button>
          </div>
          <p className="chat-copy">{auditLogStatus}</p>
          <div className="list-stack">
            {auditLogEntries.length === 0 ? (
              <p className="empty-copy">No audit entries yet.</p>
            ) : (
              auditLogEntries.map((entry) => (
                <div className="list-card" key={entry.id}>
                  <strong>{entry.action}</strong>
                  <p>{entry.targetType} #{entry.targetId}</p>
                  {entry.detail ? <p className="muted">{entry.detail}</p> : null}
                  <small>
                    Actor #{entry.actorUserId} • {formatDateTime(entry.createdAt)}
                  </small>
                </div>
              ))
            )}
          </div>
        </article>
      ) : null}
    </div>
  );

  const renderEvents = () => (
    <div className="page-stack">
      <section className="page-header">
        <div>
          <h2>Events</h2>
          <p className="card-copy">
            Search events, RSVP, and add them directly to your calendar.
          </p>
        </div>
      </section>

      {canCreateModerationContent(currentUser) ? (
        <article className="data-panel">
          <div className="panel-header">
            <h3>Create event</h3>
            <span className="panel-chip">Admins and moderators</span>
          </div>
          <div className="form-grid">
            <label className="field">
              <span>Title</span>
              <input
                name="title"
                value={eventForm.title}
                onChange={handleFormChange(setEventForm)}
              />
            </label>
            <label className="field">
              <span>Location</span>
              <input
                name="location"
                value={eventForm.location}
                onChange={handleFormChange(setEventForm)}
              />
            </label>
          </div>
          <div className="form-grid">
            <label className="field">
              <span>Date and time</span>
              <input
                name="eventDate"
                type="datetime-local"
                value={eventForm.eventDate}
                onChange={handleFormChange(setEventForm)}
              />
            </label>
            <label className="field">
              <span>Capacity</span>
              <input
                name="capacity"
                type="number"
                min="1"
                value={eventForm.capacity}
                onChange={handleFormChange(setEventForm)}
              />
            </label>
          </div>
          <label className="field">
            <span>Description</span>
            <textarea
              name="description"
              rows="4"
              value={eventForm.description}
              onChange={handleFormChange(setEventForm)}
            />
          </label>
          <button
            type="button"
            onClick={handleCreateEvent}
            disabled={loadingAction !== null}
          >
            {loadingAction === "event" ? "Creating..." : "Create event"}
          </button>
        </article>
      ) : null}

      <article className="data-panel">
        <div className="control-row">
          <input
            placeholder="Search events"
            value={eventSearch}
            onChange={(event) => setEventSearch(event.target.value)}
          />
        </div>

        <div className="list-stack">
          {filteredEvents.length === 0 ? (
            <p className="empty-copy">No events match the current search.</p>
          ) : (
            filteredEvents.map((event) => (
              <div className="list-card" key={event.id}>
                <div className="list-topline">
                  <strong>{event.title}</strong>
                  <span>{event.location}</span>
                </div>
                <p>{event.description}</p>
                <small>
                  {formatDateTime(event.eventDate)} • {event.rsvpCount}/{event.capacity} RSVPs
                </small>
                <div className="action-inline">
                  <a
                    className="ghost-link"
                    href={buildCalendarLink(event)}
                    target="_blank"
                    rel="noreferrer"
                  >
                    Add to calendar
                  </a>
                  <a
                    className="ghost-link"
                    href={buildMapLink(event.location)}
                    target="_blank"
                    rel="noreferrer"
                  >
                    Open map
                  </a>
                  {currentUser.role === "STUDENT" ? (
                    <button
                      type="button"
                      className="secondary inline-button"
                      onClick={() => handleToggleRsvp(event)}
                      disabled={loadingAction === `rsvp-${event.id}`}
                    >
                      {loadingAction === `rsvp-${event.id}`
                        ? "Updating..."
                        : (rsvpStateByEvent[event.id] ? "Cancel RSVP" : "RSVP")}
                    </button>
                  ) : null}
                </div>
              </div>
            ))
          )}
        </div>
      </article>
    </div>
  );

  const renderGroups = () => (
    <div className="page-stack">
      <section className="page-header">
        <div>
          <h2>Study groups</h2>
          <p className="card-copy">
            Create groups, manage approvals, chat, and share resource links.
          </p>
        </div>
      </section>

      <article className="data-panel">
        <div className="panel-header">
          <h3>Create study group</h3>
          <span className="panel-chip">Use case 4</span>
        </div>
        <div className="form-grid">
          <label className="field">
            <span>Name</span>
            <input
              name="name"
              value={studyGroupForm.name}
              onChange={handleFormChange(setStudyGroupForm)}
            />
          </label>
          <label className="field">
            <span>Course</span>
            <input
              name="course"
              value={studyGroupForm.course}
              onChange={handleFormChange(setStudyGroupForm)}
            />
          </label>
        </div>
        <div className="form-grid">
          <label className="field">
            <span>Topic</span>
            <textarea
              name="topic"
              rows="3"
              value={studyGroupForm.topic}
              onChange={handleFormChange(setStudyGroupForm)}
            />
          </label>
          <label className="field">
            <span>Capacity</span>
            <input
              name="capacity"
              type="number"
              min="2"
              max="50"
              value={studyGroupForm.capacity}
              onChange={handleFormChange(setStudyGroupForm)}
            />
          </label>
        </div>
        <button
          type="button"
          onClick={handleCreateStudyGroup}
          disabled={loadingAction !== null}
        >
          {loadingAction === "study-group" ? "Creating..." : "Create group"}
        </button>
      </article>

      <div className="split-grid">
        <article className="data-panel">
          <div className="panel-header">
            <h3>Available groups</h3>
            <span className="panel-chip">Search and join</span>
          </div>
          <div className="control-row">
            <input
              placeholder="Search groups"
              value={groupSearch}
              onChange={(event) => setGroupSearch(event.target.value)}
            />
          </div>

          <div className="list-stack">
            {filteredStudyGroups.length === 0 ? (
              <p className="empty-copy">No groups match the current search.</p>
            ) : (
              filteredStudyGroups.map((group) => {
                const requestStatus = joinStatuses[group.id];
                const pendingRequests = creatorRequests[group.id] || [];
                const canChat = canOpenGroupChat(group, currentUser, joinStatuses);

                return (
                  <div className="list-card" key={group.id}>
                    <div className="list-topline">
                      <strong>
                        {group.name}
                        {unreadGroups.has(group.id) && (
                          <span className="unread-dot" />
                        )}
                      </strong>
                      <span>{group.course}</span>
                    </div>
                    <p>{group.topic}</p>
                    <small>{group.memberCount}/{group.capacity} members</small>

                    <div className="action-inline">
                      {canChat ? (
                        <button
                          type="button"
                          className="secondary inline-button"
                          onClick={() => handleOpenChat(group.id)}
                        >
                          Open chat
                        </button>
                      ) : null}

                      {!canChat && group.creatorId !== currentUser.userId ? (
                        requestStatus === "PENDING" ? (
                          <span className="info-chip">Waiting for approval</span>
                        ) : requestStatus === "REJECTED" ? (
                          <span className="info-chip muted-chip">Request rejected</span>
                        ) : (
                          <button
                            type="button"
                            className="secondary inline-button"
                            onClick={() => handleJoinStudyGroup(group.id)}
                            disabled={loadingAction === `join-${group.id}`}
                          >
                            {loadingAction === `join-${group.id}` ? "Sending..." : "Request to join"}
                          </button>
                        )
                      ) : null}
                    </div>

                    {group.creatorId === currentUser.userId && pendingRequests.length > 0 ? (
                      <div className="request-box">
                        <strong>Pending requests</strong>
                        {pendingRequests.map((requestItem) => (
                          <div className="request-row" key={requestItem.id}>
                            <span>User #{requestItem.userId}</span>
                            <div className="action-inline">
                              <button
                                type="button"
                                className="secondary inline-button"
                                onClick={() => handleJoinRequestDecision(requestItem.id, "approve")}
                                disabled={loadingAction === `approve-${requestItem.id}`}
                              >
                                Approve
                              </button>
                              <button
                                type="button"
                                className="secondary inline-button"
                                onClick={() => handleJoinRequestDecision(requestItem.id, "reject")}
                                disabled={loadingAction === `reject-${requestItem.id}`}
                              >
                                Reject
                              </button>
                            </div>
                          </div>
                        ))}
                      </div>
                    ) : null}
                  </div>
                );
              })
            )}
          </div>
        </article>

        <article className="data-panel">
          <div className="panel-header">
            <h3>Group chat and resources</h3>
            <span className="panel-chip">{selectedGroup ? selectedGroup.name : "Pick a group"}</span>
          </div>
          <p className="chat-copy">{messageStatus}</p>
          {selectedGroup && typingByGroup[selectedGroupId] ? (
            <p className="chat-copy muted">
              {Object.entries(typingByGroup[selectedGroupId])
                .filter(([userId]) => String(userId) !== String(currentUser?.userId))
                .map(([, name]) => name)
                .filter(Boolean)
                .slice(0, 3)
                .join(", ")}
              {Object.keys(typingByGroup[selectedGroupId] || {}).some(
                (userId) => String(userId) !== String(currentUser?.userId),
              )
                ? " typing..."
                : ""}
            </p>
          ) : null}

          {selectedGroup ? (
            <>
              <div className="message-list">
                {messages.length === 0 ? (
                  <p className="empty-copy">No messages yet in this group.</p>
                ) : (
                  messages.map((message) => {
                    const attachmentUrl = message.attachmentUrl
                      ? (message.attachmentUrl.startsWith("/")
                        ? `${API_BASE_URL}${message.attachmentUrl}`
                        : message.attachmentUrl)
                      : "";

                    return (
                      <div className="message-card" key={message.id}>
                        <strong>{message.senderUsername}</strong>
                        {message.type === "FILE" && attachmentUrl ? (
                          <div style={{ marginTop: 6 }}>
                            <a
                              className="ghost-link"
                              href={attachmentUrl}
                              target="_blank"
                              rel="noreferrer"
                            >
                              {message.attachmentName || message.content || "Download file"}
                            </a>
                            {message.attachmentContentType && message.attachmentContentType.startsWith("image/") ? (
                              <div style={{ marginTop: 8 }}>
                                <img
                                  alt={message.attachmentName || "Shared file"}
                                  src={attachmentUrl}
                                  style={{ width: "100%", maxWidth: 320, borderRadius: 12 }}
                                />
                              </div>
                            ) : null}
                          </div>
                        ) : (
                          <p>{message.content}</p>
                        )}
                        <small>{formatDateTime(message.sentAt)}</small>
                        {(readReceiptsByMessageId[message.id] || []).length > 0 ? (
                          <small className="muted">
                            Seen by {(readReceiptsByMessageId[message.id] || []).length}
                          </small>
                        ) : null}
                      </div>
                    );
                  })
                )}
              </div>

              <label className="field">
                <span>New message</span>
                <textarea
                  name="content"
                  rows="3"
                  value={messageForm.content}
                  onChange={handleMessageInputChange}
                />
              </label>
              <button
                type="button"
                onClick={handleSendMessage}
                disabled={loadingAction === "message"}
              >
                {loadingAction === "message" ? "Sending..." : "Send message"}
              </button>

              <label className="field" style={{ marginTop: 12 }}>
                <span>Share a file</span>
                <input type="file" onChange={handleChatFileSelected} />
                {chatFilePreviewUrl ? (
                  <img
                    alt="Selected file preview"
                    src={chatFilePreviewUrl}
                    style={{ marginTop: 10, width: "100%", maxWidth: 260, borderRadius: 12 }}
                  />
                ) : null}
                {chatFileStatus ? (
                  <div className="muted" style={{ marginTop: 6 }}>
                    {chatFileStatus}
                  </div>
                ) : null}
              </label>
              <button
                type="button"
                className="secondary"
                onClick={handleSendChatFile}
                disabled={loadingAction === "message-file"}
              >
                {loadingAction === "message-file" ? "Uploading..." : "Send file"}
              </button>

              <div className="resource-panel">
                <div className="panel-header">
                  <h3>Shared resources</h3>
                  <span className="panel-chip">Link sharing</span>
                </div>
                <div className="form-grid">
                  <label className="field">
                    <span>Title</span>
                    <input
                      name="title"
                      value={resourceForm.title}
                      onChange={handleFormChange(setResourceForm)}
                    />
                  </label>
                  <label className="field">
                    <span>URL</span>
                    <input
                      name="url"
                      value={resourceForm.url}
                      onChange={handleFormChange(setResourceForm)}
                    />
                  </label>
                </div>
                <button
                  type="button"
                  className="secondary inline-button"
                  onClick={handleAddResource}
                >
                  Share link
                </button>

                <div className="list-stack">
                  {selectedGroupResources.length === 0 ? (
                    <p className="empty-copy">No shared resources yet.</p>
                  ) : (
                    selectedGroupResources.map((resource) => (
                      <div className="list-card" key={resource.id}>
                        <strong>{resource.title}</strong>
                        <a className="ghost-link" href={resource.url} target="_blank" rel="noreferrer">
                          {resource.url}
                        </a>
                        <small>
                          Shared by {resource.addedBy} • {formatDateTime(resource.createdAt)}
                        </small>
                      </div>
                    ))
                  )}
                </div>
              </div>
            </>
          ) : (
            <p className="empty-copy">
              Open a group chat from the list to start messaging and sharing resources.
            </p>
          )}
        </article>
      </div>
    </div>
  );

  const renderNotifications = () => (
    <div className="page-stack">
      <section className="page-header">
        <div>
          <h2>Notifications</h2>
          <p className="card-copy">
            Messages, event reminders, join request updates, and moderation alerts.
          </p>
        </div>
      </section>

      <article className="data-panel">
        <div className="panel-header">
          <h3>Account notifications</h3>
          <div className="action-inline">
            <button
              type="button"
              className="secondary inline-button"
              onClick={loadServerNotifications}
            >
              Refresh
            </button>
            <button
              type="button"
              className="secondary inline-button"
              onClick={markAllServerNotificationsRead}
            >
              Mark all read
            </button>
          </div>
        </div>
        <p className="chat-copy">{serverNotificationsStatus}</p>
        <div className="list-stack">
          {serverNotifications.length === 0 ? (
            <p className="empty-copy">No saved notifications yet.</p>
          ) : (
            serverNotifications.map((item) => (
              <div className={`list-card tone-${item.tone || "info"}`} key={item.id}>
                <strong>{item.title}</strong>
                <p>{item.detail}</p>
                <small>{formatDateTime(item.createdAt)}</small>
              </div>
            ))
          )}
        </div>
      </article>

      <article className="data-panel">
        <div className="panel-header">
          <h3>Notification center</h3>
          <span className="panel-chip">
            {profilePreferences.notificationsEnabled ? "Enabled" : "Muted"}
          </span>
        </div>
        <div className="list-stack">
          {notifications.length === 0 ? (
            <p className="empty-copy">No notifications to show right now.</p>
          ) : (
            notifications.map((item) => (
              <div className={`list-card tone-${item.tone}`} key={item.id}>
                <strong>{item.title}</strong>
                <p>{item.detail}</p>
                <small>{formatDateTime(item.time)}</small>
              </div>
            ))
          )}
        </div>
      </article>

      {(currentUser?.role === "MODERATOR" || currentUser?.role === "ADMIN") ? (
        <article className="data-panel">
          <div className="panel-header">
            <h3>Moderation queue</h3>
            <button
              type="button"
              className="secondary inline-button"
              onClick={loadModerationQueue}
            >
              Refresh
            </button>
          </div>
          <p className="chat-copy">{moderationQueueStatus}</p>
          <div className="list-stack">
            {moderationQueueEntries.length === 0 ? (
              <p className="empty-copy">No reports in queue.</p>
            ) : (
              moderationQueueEntries.map((entry) => (
                <div className="list-card" key={entry.id}>
                  <strong>{entry.action}</strong>
                  <p>{entry.targetType} #{entry.targetId}</p>
                  {entry.detail ? <p className="muted">{entry.detail}</p> : null}
                  <small>Actor #{entry.actorUserId} • {formatDateTime(entry.createdAt)}</small>
                </div>
              ))
            )}
          </div>
        </article>
      ) : null}

      {(currentUser?.role === "MODERATOR" || currentUser?.role === "ADMIN") ? (
        <article className="data-panel">
          <div className="panel-header">
            <h3>Audit log</h3>
            <button
              type="button"
              className="secondary inline-button"
              onClick={loadAuditLog}
            >
              Refresh
            </button>
          </div>
          <p className="chat-copy">{auditLogStatus}</p>
          <div className="list-stack">
            {auditLogEntries.length === 0 ? (
              <p className="empty-copy">No audit entries yet.</p>
            ) : (
              auditLogEntries.map((entry) => (
                <div className="list-card" key={entry.id}>
                  <strong>{entry.action}</strong>
                  <p>{entry.targetType} #{entry.targetId}</p>
                  {entry.detail ? <p className="muted">{entry.detail}</p> : null}
                  <small>Actor #{entry.actorUserId} • {formatDateTime(entry.createdAt)}</small>
                </div>
              ))
            )}
          </div>
        </article>
      ) : null}
    </div>
  );

  const renderProfile = () => (
    <div className="page-stack">
      <section className="page-header">
        <div>
          <h2>Profile and preferences</h2>
          <p className="card-copy">
            Manage your public profile, privacy choices, and light or dark theme.
          </p>
        </div>
      </section>

      <div className="split-grid">
        <article className="data-panel">
          <div className="panel-header">
            <h3>Profile</h3>
            <span className="panel-chip">Stored with your account</span>
          </div>

          {profileCompletenessStatus ? (
            <p className="chat-copy">{profileCompletenessStatus}</p>
          ) : null}
          {profileCompleteness ? (
            <div className="status-banner status-idle">
              Profile completeness: {profileCompleteness.percent}%
              {Array.isArray(profileCompleteness.missingFields) && profileCompleteness.missingFields.length > 0 ? (
                <span className="muted">
                  {" "}
                  (missing: {profileCompleteness.missingFields.join(", ")})
                </span>
              ) : null}
            </div>
          ) : null}

          <label className="field">
            <span>Display name</span>
            <input
              name="username"
              value={backendProfileForm.username}
              onChange={handleFormChange(setBackendProfileForm)}
            />
          </label>
          <label className="field">
            <span>Major</span>
            <input
              name="major"
              placeholder="Computer Science"
              value={profilePreferences.major}
              onChange={handleFormChange(setProfilePreferences)}
            />
          </label>
          <label className="field">
            <span>Interests</span>
            <textarea
              name="interests"
              rows="3"
              placeholder="UI design, algorithms, startup events"
              value={profilePreferences.interests}
              onChange={handleFormChange(setProfilePreferences)}
            />
          </label>
          <label className="field">
            <span>Profile photo</span>
            <input
              type="file"
              accept="image/*"
              onChange={handleProfilePhotoSelected}
            />
            <div className="muted" style={{ marginTop: 6 }}>
              Uploads are stored locally on the server.
            </div>
          </label>
          {profilePhotoUploadStatus ? (
            <div className="muted" style={{ marginBottom: 10 }}>
              {profilePhotoUploadStatus}
            </div>
          ) : null}
          <button
            type="button"
            className="secondary"
            onClick={handleUploadProfilePhoto}
            disabled={loadingAction === "profile-photo"}
          >
            {loadingAction === "profile-photo" ? "Uploading..." : "Upload photo"}
          </button>
          {profilePhotoPreviewUrl || profilePreferences.profilePhotoUrl ? (
            <div className="auth-helper-card">
              <strong>Preview</strong>
              <img
                alt="Profile"
                src={profilePhotoPreviewUrl || profilePreferences.profilePhotoUrl}
                style={{ width: 80, height: 80, borderRadius: 16, objectFit: "cover" }}
              />
            </div>
          ) : null}

          <button
            type="button"
            onClick={handleSaveProfile}
            disabled={loadingAction === "profile"}
          >
            {loadingAction === "profile" ? "Saving..." : "Save profile"}
          </button>
        </article>

        <article className="data-panel">
          <div className="panel-header">
            <h3>Privacy and appearance</h3>
            <span className="panel-chip">Local preferences</span>
          </div>

          <label className="field">
            <span>Profile visibility</span>
            <select
              name="privacy"
              value={profilePreferences.privacy}
              onChange={handleFormChange(setProfilePreferences)}
            >
              <option value="Public">Public</option>
              <option value="Campus only">Campus only</option>
              <option value="Private">Private</option>
            </select>
          </label>

          <label className="toggle-row">
            <input
              name="showEmail"
              type="checkbox"
              checked={profilePreferences.showEmail}
              onChange={handleFormChange(setProfilePreferences)}
            />
            <span>Show my email inside my profile card</span>
          </label>

          <label className="toggle-row">
            <input
              name="notificationsEnabled"
              type="checkbox"
              checked={profilePreferences.notificationsEnabled}
              onChange={handleFormChange(setProfilePreferences)}
            />
            <span>Enable notification center updates</span>
          </label>

          <label className="field">
            <span>Theme</span>
            <select
              name="theme"
              value={profilePreferences.theme}
              onChange={handleFormChange(setProfilePreferences)}
            >
              <option value="light">Light</option>
              <option value="dark">Dark</option>
            </select>
          </label>
        </article>
      </div>
    </div>
  );

  const renderMenu = () => (
    <div className="page-stack">
      <section className="page-header">
        <div>
          <h2>Menu</h2>
          <p className="card-copy">
            Extra system details, demo access, and campus navigation live here instead of the main screen.
          </p>
        </div>
      </section>

      <div className="split-grid">
        <article className="data-panel">
          <div className="panel-header">
            <h3>Demo accounts</h3>
            <span className="panel-chip">Local development</span>
          </div>
          <div className="demo-grid">
            {demoAccounts.map((account) => (
              <div className="demo-card" key={account.email}>
                <span>{account.label}</span>
                <strong>{account.email}</strong>
                <p>Password: {account.password}</p>
                <button
                  type="button"
                  className="secondary"
                  onClick={() => handleFillDemoAccount(account)}
                >
                  Use this account
                </button>
              </div>
            ))}
          </div>
        </article>

        <article className="data-panel">
          <div className="panel-header">
            <h3>Campus navigation</h3>
            <span className="panel-chip">Google Maps links</span>
          </div>
          <div className="list-stack">
            {campusLocations.map((place) => (
              <div className="list-card" key={place.name}>
                <strong>{place.name}</strong>
                <p>{place.description}</p>
                <a
                  className="ghost-link"
                  href={buildMapLink(place.query)}
                  target="_blank"
                  rel="noreferrer"
                >
                  Open map
                </a>
              </div>
            ))}
          </div>
        </article>
      </div>

      <article className="data-panel">
        <div className="panel-header">
          <h3>System overview</h3>
          <span className="panel-chip">From the docs</span>
        </div>
        <div className="menu-grid">
          <div className="menu-tile">
            <span>API target</span>
            <strong>{API_BASE_URL || "Same origin via CRA proxy"}</strong>
          </div>
          <div className="menu-tile">
            <span>Main users</span>
            <strong>Students, moderators, admins</strong>
          </div>
          <div className="menu-tile">
            <span>Core flows</span>
            <strong>Feed, events, study groups, chat, notifications</strong>
          </div>
          <div className="menu-tile">
            <span>Platform</span>
            <strong>Responsive web only</strong>
          </div>
        </div>
      </article>

      {currentUser ? (
        <article className="data-panel">
          <div className="panel-header">
            <h3>Web push notifications</h3>
            <span className="panel-chip">Browser</span>
          </div>
          <p className="card-copy">
            Enable browser notifications for RSVP confirmations, join request updates, and group messages.
          </p>
          {pushStatus ? <p className="chat-copy">{pushStatus}</p> : null}
          <div className="action-inline">
            <button type="button" className="secondary inline-button" onClick={enableWebPush}>
              Enable web push
            </button>
            <button type="button" className="secondary inline-button" onClick={sendTestWebPush}>
              Send test push
            </button>
          </div>
        </article>
      ) : null}
    </div>
  );

  const renderAuth = () => {
    let authTitle = "Welcome back";
    let authCopy = "Log in from the center screen to access your campus feed, groups, and events.";
    let authFields = null;

    if (authPage === "login") {
      authFields = (
        <>
          <label className="field">
            <span>Email</span>
            <input
              name="email"
              type="email"
              placeholder="student@ada.edu.az"
              value={authForm.email}
              onChange={handleAuthChange}
            />
          </label>

          <label className="field">
            <span>Password</span>
            <input
              name="password"
              type="password"
              placeholder="Enter password"
              value={authForm.password}
              onChange={handleAuthChange}
            />
          </label>

          <div className="auth-link-row">
            <button
              type="button"
              className="ghost-action"
              onClick={() => {
                setResetStep("request");
                setResetForm({
                  ...emptyResetForm,
                  email: authForm.email.trim(),
                });
                setAuthPage("forgot");
                setStatus({
                  type: "idle",
                  message: "Enter your email to receive a password recovery code.",
                });
              }}
            >
              Forgot password?
            </button>
          </div>

          <button
            type="button"
            onClick={() => handleAuthAction("login")}
            disabled={loadingAction !== null}
          >
            {loadingAction === "login" ? "Logging in..." : "Login"}
          </button>
        </>
      );
    }

    if (authPage === "register") {
      authTitle = "Create your account";
      authCopy = "Register here first. After that, we will send you to the OTP verification page.";
      authFields = (
        <>
          <label className="field">
            <span>Username</span>
            <input
              name="username"
              placeholder="campusflow-user"
              value={authForm.username}
              onChange={handleAuthChange}
            />
          </label>

          <label className="field">
            <span>Email</span>
            <input
              name="email"
              type="email"
              placeholder="student@ada.edu.az"
              value={authForm.email}
              onChange={handleAuthChange}
            />
          </label>

          <label className="field">
            <span>Password</span>
            <input
              name="password"
              type="password"
              placeholder="Create password"
              value={authForm.password}
              onChange={handleAuthChange}
            />
          </label>

          <button
            type="button"
            onClick={() => handleAuthAction("register")}
            disabled={loadingAction !== null}
          >
            {loadingAction === "register" ? "Registering..." : "Create account"}
          </button>
        </>
      );
    }

    if (authPage === "verify") {
      authTitle = "Verify your email";
      authCopy = "We sent an OTP to your email. Enter it here to activate your account before logging in.";
      authFields = (
        <>
          <label className="field">
            <span>Email</span>
            <input
              name="email"
              type="email"
              placeholder="student@ada.edu.az"
              value={authForm.email}
              onChange={handleAuthChange}
            />
          </label>

          <label className="field">
            <span>OTP code</span>
            <input
              name="otpCode"
              inputMode="numeric"
              maxLength="6"
              placeholder="123456"
              value={authForm.otpCode}
              onChange={handleAuthChange}
            />
          </label>

          <div className="action-inline auth-actions">
            <button
              type="button"
              className="secondary wide-action"
              onClick={() => handleOtpAction("resend")}
              disabled={loadingAction !== null}
            >
              {loadingAction === "resend" ? "Sending..." : "Resend OTP"}
            </button>
            <button
              type="button"
              className="wide-action"
              onClick={() => handleOtpAction("verify")}
              disabled={loadingAction !== null}
            >
              {loadingAction === "verify" ? "Verifying..." : "Verify email"}
            </button>
          </div>
        </>
      );
    }

    if (authPage === "forgot") {
      authTitle = "Recover your password";
      authCopy = resetStep === "confirm"
        ? "Enter the reset code from email and choose a new password."
        : "Enter your email to receive a 6-digit password reset code.";
      authFields = (
        <>
          <label className="field">
            <span>Email</span>
            <input
              name="email"
              type="email"
              placeholder="student@ada.edu.az"
              value={resetForm.email}
              onChange={handleResetChange}
            />
          </label>

          {resetStep === "confirm" ? (
            <>
              <label className="field">
                <span>Reset code</span>
                <input
                  name="otpCode"
                  inputMode="numeric"
                  maxLength="6"
                  placeholder="123456"
                  value={resetForm.otpCode}
                  onChange={handleResetChange}
                />
              </label>

              <label className="field">
                <span>New password</span>
                <input
                  name="newPassword"
                  type="password"
                  placeholder="Enter a new password"
                  value={resetForm.newPassword}
                  onChange={handleResetChange}
                />
              </label>

              <label className="field">
                <span>Confirm new password</span>
                <input
                  name="confirmPassword"
                  type="password"
                  placeholder="Repeat the new password"
                  value={resetForm.confirmPassword}
                  onChange={handleResetChange}
                />
              </label>

              <div className="action-inline auth-actions">
                <button
                  type="button"
                  className="secondary wide-action"
                  onClick={() => {
                    setResetStep("request");
                    setResetForm((current) => ({
                      ...emptyResetForm,
                      email: current.email,
                    }));
                    setStatus({
                      type: "idle",
                      message: "Request a fresh reset code if needed.",
                    });
                  }}
                  disabled={loadingAction !== null}
                >
                  Edit email
                </button>
                <button
                  type="button"
                  className="wide-action"
                  onClick={handleResetPassword}
                  disabled={loadingAction !== null}
                >
                  {loadingAction === "reset-password" ? "Resetting..." : "Reset password"}
                </button>
              </div>
            </>
          ) : (
            <button
              type="button"
              className="wide-action"
              onClick={handleForgotPasswordRequest}
              disabled={loadingAction !== null}
            >
              {loadingAction === "forgot-password" ? "Sending..." : "Send reset code"}
            </button>
          )}
        </>
      );
    }

    return (
      <div className="auth-center-shell">
        <section className="auth-center-card">
          <span className="eyebrow">CampusFlow</span>
          <h1 className="auth-hero-title">Campus Life</h1>
          <p className="hero-copy auth-hero-copy">
            A cleaner student experience for events, communities, study groups, and campus updates.
          </p>

          <div className="auth-switcher">
            <button
              type="button"
              className={`nav-pill auth-tab ${authPage === "login" ? "nav-pill-active" : ""}`}
              onClick={() => setAuthPage("login")}
            >
              Login
            </button>
            <button
              type="button"
              className={`nav-pill auth-tab ${authPage === "register" ? "nav-pill-active" : ""}`}
              onClick={() => setAuthPage("register")}
            >
              Register
            </button>
          </div>

          <div className="auth-panel auth-panel-centered">
            <div className="section-heading">
              <div>
                <h2>{authTitle}</h2>
                <p className="card-copy">{authCopy}</p>
              </div>
              <span className="section-chip">{authPage}</span>
            </div>

            {authFields}

            <div className={`status-banner status-${status.type}`} role="status">
              {status.message}
            </div>

            {authPage === "verify" ? (
              <div className="auth-helper-card">
                <strong>Verification step</strong>
                <p>
                  This page opens right after registration so the user can enter the OTP from email in a separate place.
                </p>
                <button
                  type="button"
                  className="ghost-action"
                  onClick={() => setAuthPage("login")}
                >
                  Back to login
                </button>
              </div>
            ) : null}

            {authPage === "forgot" ? (
              <div className="auth-helper-card">
                <strong>Password recovery</strong>
                <p>
                  Request the code first, then enter the code and your new password here.
                </p>
                <button
                  type="button"
                  className="ghost-action"
                  onClick={() => {
                    setAuthPage("login");
                    setResetStep("request");
                    setResetForm(emptyResetForm);
                  }}
                >
                  Back to login
                </button>
              </div>
            ) : null}

            {authPage === "login" ? (
              <div className="auth-bottom-note">
                <span>New here?</span>
                <button
                  type="button"
                  className="ghost-action"
                  onClick={() => setAuthPage("register")}
                >
                  Create account
                </button>
              </div>
            ) : null}

            {authPage === "register" ? (
              <div className="auth-bottom-note">
                <span>Already have an account?</span>
                <button
                  type="button"
                  className="ghost-action"
                  onClick={() => setAuthPage("login")}
                >
                  Back to login
                </button>
              </div>
            ) : null}
          </div>
        </section>
      </div>
    );
  };

  const navigationItems = currentUser ? privateNavigation : publicNavigation;

  let pageContent;
  if (!currentUser) {
    pageContent = activeView === "menu" ? renderMenu() : renderAuth();
  } else {
    switch (activeView) {
      case "feed":
        pageContent = renderFeed();
        break;
      case "events":
        pageContent = renderEvents();
        break;
      case "groups":
        pageContent = renderGroups();
        break;
      case "notifications":
        pageContent = renderNotifications();
        break;
      case "profile":
        pageContent = renderProfile();
        break;
      case "menu":
        pageContent = renderMenu();
        break;
      case "dashboard":
      default:
        pageContent = renderDashboard();
        break;
    }
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div className="brand-block">
          <span className="brand-mark">CF</span>
          <div>
            <strong>CampusFlow</strong>
            <p>Community platform for students</p>
          </div>
        </div>

        <nav className="topnav">
          {navigationItems.map((item) => (
            <button
              key={item.id}
              type="button"
              className={`nav-pill ${activeView === item.id ? "nav-pill-active" : ""}`}
              onClick={() => {
                setActiveView(item.id);
                if (!currentUser && item.id === "auth") {
                  setAuthPage("login");
                }
              }}
            >
              {item.label}
            </button>
          ))}
        </nav>

        {currentUser ? (
          <div className="top-actions">
            <span className="top-user-pill">
              {currentUser.username} · {formatRole(currentUser.role)}
            </span>
            <button
              type="button"
              className="secondary"
              onClick={() => setActiveView("menu")}
            >
              More
            </button>
            <button
              type="button"
              className="secondary"
              onClick={() => loadWorkspace(currentUser)}
            >
              Refresh
            </button>
            <button
              type="button"
              className="secondary"
              onClick={handleLogout}
            >
              Logout
            </button>
          </div>
        ) : null}
      </header>

      <section className="main-surface">
        {pageContent}
      </section>
    </main>
  );
}

export default App;
