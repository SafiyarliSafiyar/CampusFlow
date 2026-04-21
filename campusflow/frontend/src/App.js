import { useEffect, useMemo, useState } from "react";
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
  { id: "menu", label: "Menu" },
];

const emptyAuthForm = {
  username: "",
  email: "",
  password: "",
  otpCode: "",
};

const emptyResetForm = {
  email: "",
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
  const [feedSearch, setFeedSearch] = useState("");
  const [feedTypeFilter, setFeedTypeFilter] = useState("ALL");
  const [eventSearch, setEventSearch] = useState("");
  const [groupSearch, setGroupSearch] = useState("");
  const [postForm, setPostForm] = useState(emptyPostForm);
  const [eventForm, setEventForm] = useState(emptyEventForm);
  const [studyGroupForm, setStudyGroupForm] = useState(emptyStudyGroupForm);
  const [backendProfileForm, setBackendProfileForm] = useState(emptyBackendProfileForm);
  const [profilePreferences, setProfilePreferences] = useState(emptyPreferenceProfile);
  const [messageForm, setMessageForm] = useState(emptyMessageForm);
  const [resourceForm, setResourceForm] = useState(emptyResourceForm);
  const [sharedResources, setSharedResources] = useState({});
  const [postInteractions, setPostInteractions] = useState({});
  const [rsvpStateByEvent, setRsvpStateByEvent] = useState({});

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

  const updateStoredUser = (nextUser) => {
    setCurrentUser(nextUser);
    if (nextUser) {
      window.localStorage.setItem("campusflowUser", JSON.stringify(nextUser));
    } else {
      window.localStorage.removeItem("campusflowUser");
    }
  };

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
      const [feedData, eventData, groupData] = await Promise.all([
        request("/api/v1/feed?page=0&size=20"),
        request("/api/v1/events"),
        request("/api/v1/study-groups"),
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
    setSelectedGroupId(null);
    setMessages([]);
    setMessageForm(emptyMessageForm);
    setRsvpStateByEvent({});
    setActiveView("auth");
    setAuthPage("login");
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
  };

  const handleForgotPasswordRequest = () => {
    setStatus({
      type: "idle",
      message: "Forgot password page is ready on the frontend, but sending recovery emails needs backend support. Right now only login, register, and OTP verification are connected.",
    });
  };

  const handleSaveProfile = async () => {
    setLoadingAction("profile");
    setWorkspaceNotice({
      type: "loading",
      message: "Updating profile...",
    });

    try {
      const data = await request("/api/v1/users/profile", {
        method: "PUT",
        body: {
          username: backendProfileForm.username.trim(),
        },
      });
      const nextUser = {
        ...currentUser,
        username: data?.username || backendProfileForm.username.trim(),
      };
      updateStoredUser(nextUser);
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
    setPostInteractions((current) => {
      const existing = current[postId] || {};
      const nextReported = !existing.reported;
      return {
        ...current,
        [postId]: {
          ...existing,
          reported: nextReported,
          reportedAt: nextReported ? new Date().toISOString() : null,
        },
      };
    });
  };

  const handleOpenChat = async (groupId) => {
    setSelectedGroupId(groupId);
    await loadMessages(groupId);
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
        </article>

        <article className="data-panel">
          <div className="panel-header">
            <h3>Next events</h3>
            <span className="panel-chip">Upcoming</span>
          </div>
          <div className="list-stack">
            {events.length === 0 ? (
              <p className="empty-copy">No events available yet.</p>
            ) : (
              events.slice(0, 3).map((event) => (
                <div className="list-card" key={event.id}>
                  <strong>{event.title}</strong>
                  <p>{event.location}</p>
                  <small>{formatDateTime(event.eventDate)}</small>
                </div>
              ))
            )}
          </div>
        </article>

        <article className="data-panel">
          <div className="panel-header">
            <h3>Quick actions</h3>
            <span className="panel-chip">Shortcuts</span>
          </div>
          <div className="action-grid">
            <button type="button" className="secondary tile-button" onClick={() => setActiveView("feed")}>
              Community feed
            </button>
            <button type="button" className="secondary tile-button" onClick={() => setActiveView("groups")}>
              Study groups
            </button>
            <button type="button" className="secondary tile-button" onClick={() => setActiveView("events")}>
              Event RSVP
            </button>
            <button type="button" className="secondary tile-button" onClick={() => setActiveView("profile")}>
              Profile settings
            </button>
            <button type="button" className="secondary tile-button" onClick={() => setActiveView("menu")}>
              Open menu
            </button>
            <button type="button" className="secondary tile-button" onClick={() => loadWorkspace(currentUser)}>
              Refresh workspace
            </button>
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
                      <strong>{group.name}</strong>
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

          {selectedGroup ? (
            <>
              <div className="message-list">
                {messages.length === 0 ? (
                  <p className="empty-copy">No messages yet in this group.</p>
                ) : (
                  messages.map((message) => (
                    <div className="message-card" key={message.id}>
                      <strong>{message.senderUsername}</strong>
                      <p>{message.content}</p>
                      <small>{formatDateTime(message.sentAt)}</small>
                    </div>
                  ))
                )}
              </div>

              <label className="field">
                <span>New message</span>
                <textarea
                  name="content"
                  rows="3"
                  value={messageForm.content}
                  onChange={handleFormChange(setMessageForm)}
                />
              </label>
              <button
                type="button"
                onClick={handleSendMessage}
                disabled={loadingAction === "message"}
              >
                {loadingAction === "message" ? "Sending..." : "Send message"}
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
            <span>Privacy level</span>
            <select
              name="privacy"
              value={profilePreferences.privacy}
              onChange={handleFormChange(setProfilePreferences)}
            >
              <option value="Campus only">Campus only</option>
              <option value="Study groups only">Study groups only</option>
              <option value="Visible to moderators">Visible to moderators</option>
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
                setResetForm((current) => ({
                  ...current,
                  email: authForm.email.trim(),
                }));
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
      authCopy = "This page is separated in the UI, but email recovery still needs a backend endpoint before it can truly send reset emails.";
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

          <button
            type="button"
            className="wide-action"
            onClick={handleForgotPasswordRequest}
          >
            Continue
          </button>
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
                  The page design is ready, but email recovery still needs backend support before it can actually send reset emails.
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
