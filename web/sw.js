/* 강호유랑 오프라인 실행용: 게임 파일은 인터넷이 되면 최신으로, 안 되면 저장해 둔 것으로 */
const C = 'gangho-v1.1.1';
const FONT = 'gangho-fonts';
const CORE = ['./', './index.html', './manifest.webmanifest', './icon-192.png', './icon-512.png', './apple-touch-icon.png'];

self.addEventListener('install', e => {
  e.waitUntil(caches.open(C).then(c => c.addAll(CORE)));
  self.skipWaiting();
});

self.addEventListener('activate', e => {
  e.waitUntil(caches.keys().then(ks => Promise.all(ks.filter(k => k !== C && k !== FONT).map(k => caches.delete(k)))));
  self.clients.claim();
});

self.addEventListener('fetch', e => {
  const r = e.request;
  if (r.method !== 'GET') return;
  const u = new URL(r.url);
  const put = (name, res) => { const cp = res.clone(); caches.open(name).then(c => c.put(r, cp)); return res; };

  if (u.origin === location.origin) {
    if (r.mode === 'navigate' || u.pathname.endsWith('/') || u.pathname.endsWith('.html')) {
      e.respondWith(fetch(r).then(res => put(C, res)).catch(() => caches.match(r).then(m => m || caches.match('./index.html'))));
    } else {
      e.respondWith(caches.match(r).then(m => m || fetch(r).then(res => put(C, res))));
    }
    return;
  }
  if (u.hostname.endsWith('fonts.googleapis.com') || u.hostname.endsWith('fonts.gstatic.com')) {
    e.respondWith(caches.match(r).then(m => m || fetch(r).then(res => put(FONT, res))));
  }
});
