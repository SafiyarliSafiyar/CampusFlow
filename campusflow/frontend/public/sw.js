self.addEventListener("push", (event) => {
  let data = {};
  try {
    data = event.data ? event.data.json() : {};
  } catch (_e) {
    data = { message: event.data ? event.data.text() : "" };
  }

  const title = data.title || "CampusFlow";
  const options = {
    body: data.body || data.message || "You have a new notification.",
    icon: "/logo192.png",
    badge: "/logo192.png",
    data: data.data || {},
  };

  event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener("notificationclick", (event) => {
  event.notification.close();
  event.waitUntil(
    (async () => {
      const targetUrl = event.notification?.data?.url || "/";
      const allClients = await clients.matchAll({ type: "window", includeUncontrolled: true });
      if (allClients.length > 0) {
        allClients[0].focus();
        return;
      }
      await clients.openWindow(targetUrl);
    })(),
  );
});
