try{self["workbox:core:7.0.0"]&&_()}catch{}const O=(s,...e)=>{let t=s;return e.length>0&&(t+=` :: ${JSON.stringify(e)}`),t},M=O;class l extends Error{constructor(e,t){const a=M(e,t);super(a),this.name=e,this.details=t}}const S=new Set,f={googleAnalytics:"googleAnalytics",precache:"precache-v2",prefix:"workbox",runtime:"runtime",suffix:typeof registration<"u"?registration.scope:""},R=s=>[f.prefix,s,f.suffix].filter(e=>e&&e.length>0).join("-"),W=s=>{for(const e of Object.keys(f))s(e)},I={updateDetails:s=>{W(e=>{typeof s[e]=="string"&&(f[e]=s[e])})},getGoogleAnalyticsName:s=>s||R(f.googleAnalytics),getPrecacheName:s=>s||R(f.precache),getPrefix:()=>f.prefix,getRuntimeName:s=>s||R(f.runtime),getSuffix:()=>f.suffix};function j(s,e){const t=new URL(s);for(const a of e)t.searchParams.delete(a);return t.href}async function F(s,e,t,a){const i=j(e.url,t);if(e.url===i)return s.match(e,a);const n=Object.assign(Object.assign({},a),{ignoreSearch:!0}),r=await s.keys(e,n);for(const c of r){const o=j(c.url,t);if(i===o)return s.match(c,a)}}let g;function q(){if(g===void 0){const s=new Response("");if("body"in s)try{new Response(s.body),g=!0}catch{g=!1}g=!1}return g}class H{constructor(){this.promise=new Promise((e,t)=>{this.resolve=e,this.reject=t})}}async function B(){for(const s of S)await s()}const $=s=>new URL(String(s),location.href).href.replace(new RegExp(`^${location.origin}`),"");function T(s){return new Promise(e=>setTimeout(e,s))}function U(s,e){const t=e();return s.waitUntil(t),t}async function G(s,e){let t=null;if(s.url&&(t=new URL(s.url).origin),t!==self.location.origin)throw new l("cross-origin-copy-response",{origin:t});const a=s.clone(),i={headers:new Headers(a.headers),status:a.status,statusText:a.statusText},n=e?e(i):i,r=q()?a.body:await a.blob();return new Response(r,n)}function z(){self.addEventListener("activate",()=>self.clients.claim())}try{self["workbox:precaching:7.0.0"]&&_()}catch{}const Q="__WB_REVISION__";function J(s){if(!s)throw new l("add-to-cache-list-unexpected-type",{entry:s});if(typeof s=="string"){const n=new URL(s,location.href);return{cacheKey:n.href,url:n.href}}const{revision:e,url:t}=s;if(!t)throw new l("add-to-cache-list-unexpected-type",{entry:s});if(!e){const n=new URL(t,location.href);return{cacheKey:n.href,url:n.href}}const a=new URL(t,location.href),i=new URL(t,location.href);return a.searchParams.set(Q,e),{cacheKey:a.href,url:i.href}}class X{constructor(){this.updatedURLs=[],this.notUpdatedURLs=[],this.handlerWillStart=async({request:e,state:t})=>{t&&(t.originalRequest=e)},this.cachedResponseWillBeUsed=async({event:e,state:t,cachedResponse:a})=>{if(e.type==="install"&&t&&t.originalRequest&&t.originalRequest instanceof Request){const i=t.originalRequest.url;a?this.notUpdatedURLs.push(i):this.updatedURLs.push(i)}return a}}}class Y{constructor({precacheController:e}){this.cacheKeyWillBeUsed=async({request:t,params:a})=>{const i=(a==null?void 0:a.cacheKey)||this._precacheController.getCacheKeyForURL(t.url);return i?new Request(i,{headers:t.headers}):t},this._precacheController=e}}try{self["workbox:strategies:7.0.0"]&&_()}catch{}function y(s){return typeof s=="string"?new Request(s):s}class Z{constructor(e,t){this._cacheKeys={},Object.assign(this,t),this.event=t.event,this._strategy=e,this._handlerDeferred=new H,this._extendLifetimePromises=[],this._plugins=[...e.plugins],this._pluginStateMap=new Map;for(const a of this._plugins)this._pluginStateMap.set(a,{});this.event.waitUntil(this._handlerDeferred.promise)}async fetch(e){const{event:t}=this;let a=y(e);if(a.mode==="navigate"&&t instanceof FetchEvent&&t.preloadResponse){const r=await t.preloadResponse;if(r)return r}const i=this.hasCallback("fetchDidFail")?a.clone():null;try{for(const r of this.iterateCallbacks("requestWillFetch"))a=await r({request:a.clone(),event:t})}catch(r){if(r instanceof Error)throw new l("plugin-error-request-will-fetch",{thrownErrorMessage:r.message})}const n=a.clone();try{let r;r=await fetch(a,a.mode==="navigate"?void 0:this._strategy.fetchOptions);for(const c of this.iterateCallbacks("fetchDidSucceed"))r=await c({event:t,request:n,response:r});return r}catch(r){throw i&&await this.runCallbacks("fetchDidFail",{error:r,event:t,originalRequest:i.clone(),request:n.clone()}),r}}async fetchAndCachePut(e){const t=await this.fetch(e),a=t.clone();return this.waitUntil(this.cachePut(e,a)),t}async cacheMatch(e){const t=y(e);let a;const{cacheName:i,matchOptions:n}=this._strategy,r=await this.getCacheKey(t,"read"),c=Object.assign(Object.assign({},n),{cacheName:i});a=await caches.match(r,c);for(const o of this.iterateCallbacks("cachedResponseWillBeUsed"))a=await o({cacheName:i,matchOptions:n,cachedResponse:a,request:r,event:this.event})||void 0;return a}async cachePut(e,t){const a=y(e);await T(0);const i=await this.getCacheKey(a,"write");if(!t)throw new l("cache-put-with-no-response",{url:$(i.url)});const n=await this._ensureResponseSafeToCache(t);if(!n)return!1;const{cacheName:r,matchOptions:c}=this._strategy,o=await self.caches.open(r),d=this.hasCallback("cacheDidUpdate"),p=d?await F(o,i.clone(),["__WB_REVISION__"],c):null;try{await o.put(i,d?n.clone():n)}catch(u){if(u instanceof Error)throw u.name==="QuotaExceededError"&&await B(),u}for(const u of this.iterateCallbacks("cacheDidUpdate"))await u({cacheName:r,oldResponse:p,newResponse:n.clone(),request:i,event:this.event});return!0}async getCacheKey(e,t){const a=`${e.url} | ${t}`;if(!this._cacheKeys[a]){let i=e;for(const n of this.iterateCallbacks("cacheKeyWillBeUsed"))i=y(await n({mode:t,request:i,event:this.event,params:this.params}));this._cacheKeys[a]=i}return this._cacheKeys[a]}hasCallback(e){for(const t of this._strategy.plugins)if(e in t)return!0;return!1}async runCallbacks(e,t){for(const a of this.iterateCallbacks(e))await a(t)}*iterateCallbacks(e){for(const t of this._strategy.plugins)if(typeof t[e]=="function"){const a=this._pluginStateMap.get(t);yield n=>{const r=Object.assign(Object.assign({},n),{state:a});return t[e](r)}}}waitUntil(e){return this._extendLifetimePromises.push(e),e}async doneWaiting(){let e;for(;e=this._extendLifetimePromises.shift();)await e}destroy(){this._handlerDeferred.resolve(null)}async _ensureResponseSafeToCache(e){let t=e,a=!1;for(const i of this.iterateCallbacks("cacheWillUpdate"))if(t=await i({request:this.request,response:t,event:this.event})||void 0,a=!0,!t)break;return a||t&&t.status!==200&&(t=void 0),t}}class k{constructor(e={}){this.cacheName=I.getRuntimeName(e.cacheName),this.plugins=e.plugins||[],this.fetchOptions=e.fetchOptions,this.matchOptions=e.matchOptions}handle(e){const[t]=this.handleAll(e);return t}handleAll(e){e instanceof FetchEvent&&(e={event:e,request:e.request});const t=e.event,a=typeof e.request=="string"?new Request(e.request):e.request,i="params"in e?e.params:void 0,n=new Z(this,{event:t,request:a,params:i}),r=this._getResponse(n,a,t),c=this._awaitComplete(r,n,a,t);return[r,c]}async _getResponse(e,t,a){await e.runCallbacks("handlerWillStart",{event:a,request:t});let i;try{if(i=await this._handle(t,e),!i||i.type==="error")throw new l("no-response",{url:t.url})}catch(n){if(n instanceof Error){for(const r of e.iterateCallbacks("handlerDidError"))if(i=await r({error:n,event:a,request:t}),i)break}if(!i)throw n}for(const n of e.iterateCallbacks("handlerWillRespond"))i=await n({event:a,request:t,response:i});return i}async _awaitComplete(e,t,a,i){let n,r;try{n=await e}catch{}try{await t.runCallbacks("handlerDidRespond",{event:i,request:a,response:n}),await t.doneWaiting()}catch(c){c instanceof Error&&(r=c)}if(await t.runCallbacks("handlerDidComplete",{event:i,request:a,response:n,error:r}),t.destroy(),r)throw r}}class h extends k{constructor(e={}){e.cacheName=I.getPrecacheName(e.cacheName),super(e),this._fallbackToNetwork=e.fallbackToNetwork!==!1,this.plugins.push(h.copyRedirectedCacheableResponsesPlugin)}async _handle(e,t){const a=await t.cacheMatch(e);return a||(t.event&&t.event.type==="install"?await this._handleInstall(e,t):await this._handleFetch(e,t))}async _handleFetch(e,t){let a;const i=t.params||{};if(this._fallbackToNetwork){const n=i.integrity,r=e.integrity,c=!r||r===n;a=await t.fetch(new Request(e,{integrity:e.mode!=="no-cors"?r||n:void 0})),n&&c&&e.mode!=="no-cors"&&(this._useDefaultCacheabilityPluginIfNeeded(),await t.cachePut(e,a.clone()))}else throw new l("missing-precache-entry",{cacheName:this.cacheName,url:e.url});return a}async _handleInstall(e,t){this._useDefaultCacheabilityPluginIfNeeded();const a=await t.fetch(e);if(!await t.cachePut(e,a.clone()))throw new l("bad-precaching-response",{url:e.url,status:a.status});return a}_useDefaultCacheabilityPluginIfNeeded(){let e=null,t=0;for(const[a,i]of this.plugins.entries())i!==h.copyRedirectedCacheableResponsesPlugin&&(i===h.defaultPrecacheCacheabilityPlugin&&(e=a),i.cacheWillUpdate&&t++);t===0?this.plugins.push(h.defaultPrecacheCacheabilityPlugin):t>1&&e!==null&&this.plugins.splice(e,1)}}h.defaultPrecacheCacheabilityPlugin={async cacheWillUpdate({response:s}){return!s||s.status>=400?null:s}};h.copyRedirectedCacheableResponsesPlugin={async cacheWillUpdate({response:s}){return s.redirected?await G(s):s}};class ee{constructor({cacheName:e,plugins:t=[],fallbackToNetwork:a=!0}={}){this._urlsToCacheKeys=new Map,this._urlsToCacheModes=new Map,this._cacheKeysToIntegrities=new Map,this._strategy=new h({cacheName:I.getPrecacheName(e),plugins:[...t,new Y({precacheController:this})],fallbackToNetwork:a}),this.install=this.install.bind(this),this.activate=this.activate.bind(this)}get strategy(){return this._strategy}precache(e){this.addToCacheList(e),this._installAndActiveListenersAdded||(self.addEventListener("install",this.install),self.addEventListener("activate",this.activate),this._installAndActiveListenersAdded=!0)}addToCacheList(e){const t=[];for(const a of e){typeof a=="string"?t.push(a):a&&a.revision===void 0&&t.push(a.url);const{cacheKey:i,url:n}=J(a),r=typeof a!="string"&&a.revision?"reload":"default";if(this._urlsToCacheKeys.has(n)&&this._urlsToCacheKeys.get(n)!==i)throw new l("add-to-cache-list-conflicting-entries",{firstEntry:this._urlsToCacheKeys.get(n),secondEntry:i});if(typeof a!="string"&&a.integrity){if(this._cacheKeysToIntegrities.has(i)&&this._cacheKeysToIntegrities.get(i)!==a.integrity)throw new l("add-to-cache-list-conflicting-integrities",{url:n});this._cacheKeysToIntegrities.set(i,a.integrity)}if(this._urlsToCacheKeys.set(n,i),this._urlsToCacheModes.set(n,r),t.length>0){const c=`Workbox is precaching URLs without revision info: ${t.join(", ")}
This is generally NOT safe. Learn more at https://bit.ly/wb-precache`;console.warn(c)}}}install(e){return U(e,async()=>{const t=new X;this.strategy.plugins.push(t);for(const[n,r]of this._urlsToCacheKeys){const c=this._cacheKeysToIntegrities.get(r),o=this._urlsToCacheModes.get(n),d=new Request(n,{integrity:c,cache:o,credentials:"same-origin"});await Promise.all(this.strategy.handleAll({params:{cacheKey:r},request:d,event:e}))}const{updatedURLs:a,notUpdatedURLs:i}=t;return{updatedURLs:a,notUpdatedURLs:i}})}activate(e){return U(e,async()=>{const t=await self.caches.open(this.strategy.cacheName),a=await t.keys(),i=new Set(this._urlsToCacheKeys.values()),n=[];for(const r of a)i.has(r.url)||(await t.delete(r),n.push(r.url));return{deletedURLs:n}})}getURLsToCacheKeys(){return this._urlsToCacheKeys}getCachedURLs(){return[...this._urlsToCacheKeys.keys()]}getCacheKeyForURL(e){const t=new URL(e,location.href);return this._urlsToCacheKeys.get(t.href)}getIntegrityForCacheKey(e){return this._cacheKeysToIntegrities.get(e)}async matchPrecache(e){const t=e instanceof Request?e.url:e,a=this.getCacheKeyForURL(t);if(a)return(await self.caches.open(this.strategy.cacheName)).match(a)}createHandlerBoundToURL(e){const t=this.getCacheKeyForURL(e);if(!t)throw new l("non-precached-url",{url:e});return a=>(a.request=new Request(e),a.params=Object.assign({cacheKey:t},a.params),this.strategy.handle(a))}}let C;const v=()=>(C||(C=new ee),C);try{self["workbox:routing:7.0.0"]&&_()}catch{}const K="GET",m=s=>s&&typeof s=="object"?s:{handle:s};class b{constructor(e,t,a=K){this.handler=m(t),this.match=e,this.method=a}setCatchHandler(e){this.catchHandler=m(e)}}class te extends b{constructor(e,t,a){const i=({url:n})=>{const r=e.exec(n.href);if(r&&!(n.origin!==location.origin&&r.index!==0))return r.slice(1)};super(i,t,a)}}class ae{constructor(){this._routes=new Map,this._defaultHandlerMap=new Map}get routes(){return this._routes}addFetchListener(){self.addEventListener("fetch",e=>{const{request:t}=e,a=this.handleRequest({request:t,event:e});a&&e.respondWith(a)})}addCacheListener(){self.addEventListener("message",e=>{if(e.data&&e.data.type==="CACHE_URLS"){const{payload:t}=e.data,a=Promise.all(t.urlsToCache.map(i=>{typeof i=="string"&&(i=[i]);const n=new Request(...i);return this.handleRequest({request:n,event:e})}));e.waitUntil(a),e.ports&&e.ports[0]&&a.then(()=>e.ports[0].postMessage(!0))}})}handleRequest({request:e,event:t}){const a=new URL(e.url,location.href);if(!a.protocol.startsWith("http"))return;const i=a.origin===location.origin,{params:n,route:r}=this.findMatchingRoute({event:t,request:e,sameOrigin:i,url:a});let c=r&&r.handler;const o=e.method;if(!c&&this._defaultHandlerMap.has(o)&&(c=this._defaultHandlerMap.get(o)),!c)return;let d;try{d=c.handle({url:a,request:e,event:t,params:n})}catch(u){d=Promise.reject(u)}const p=r&&r.catchHandler;return d instanceof Promise&&(this._catchHandler||p)&&(d=d.catch(async u=>{if(p)try{return await p.handle({url:a,request:e,event:t,params:n})}catch(D){D instanceof Error&&(u=D)}if(this._catchHandler)return this._catchHandler.handle({url:a,request:e,event:t});throw u})),d}findMatchingRoute({url:e,sameOrigin:t,request:a,event:i}){const n=this._routes.get(a.method)||[];for(const r of n){let c;const o=r.match({url:e,sameOrigin:t,request:a,event:i});if(o)return c=o,(Array.isArray(c)&&c.length===0||o.constructor===Object&&Object.keys(o).length===0||typeof o=="boolean")&&(c=void 0),{route:r,params:c}}return{}}setDefaultHandler(e,t=K){this._defaultHandlerMap.set(t,m(e))}setCatchHandler(e){this._catchHandler=m(e)}registerRoute(e){this._routes.has(e.method)||this._routes.set(e.method,[]),this._routes.get(e.method).push(e)}unregisterRoute(e){if(!this._routes.has(e.method))throw new l("unregister-route-but-not-found-with-method",{method:e.method});const t=this._routes.get(e.method).indexOf(e);if(t>-1)this._routes.get(e.method).splice(t,1);else throw new l("unregister-route-route-not-registered")}}let w;const se=()=>(w||(w=new ae,w.addFetchListener(),w.addCacheListener()),w);function x(s,e,t){let a;if(typeof s=="string"){const n=new URL(s,location.href),r=({url:c})=>c.href===n.href;a=new b(r,e,t)}else if(s instanceof RegExp)a=new te(s,e,t);else if(typeof s=="function")a=new b(s,e,t);else if(s instanceof b)a=s;else throw new l("unsupported-route-type",{moduleName:"workbox-routing",funcName:"registerRoute",paramName:"capture"});return se().registerRoute(a),a}function ie(s,e=[]){for(const t of[...s.searchParams.keys()])e.some(a=>a.test(t))&&s.searchParams.delete(t);return s}function*ne(s,{ignoreURLParametersMatching:e=[/^utm_/,/^fbclid$/],directoryIndex:t="index.html",cleanURLs:a=!0,urlManipulation:i}={}){const n=new URL(s,location.href);n.hash="",yield n.href;const r=ie(n,e);if(yield r.href,t&&r.pathname.endsWith("/")){const c=new URL(r.href);c.pathname+=t,yield c.href}if(a){const c=new URL(r.href);c.pathname+=".html",yield c.href}if(i){const c=i({url:n});for(const o of c)yield o.href}}class re extends b{constructor(e,t){const a=({request:i})=>{const n=e.getURLsToCacheKeys();for(const r of ne(i.url,t)){const c=n.get(r);if(c){const o=e.getIntegrityForCacheKey(c);return{cacheKey:c,integrity:o}}}};super(a,e.strategy)}}function ce(s){const e=v(),t=new re(e,s);x(t)}function L(s){return v().getCacheKeyForURL(s)}function P(s){return v().matchPrecache(s)}function oe(s){v().precache(s)}function le(s,e){oe(s),ce(e)}class de extends b{constructor(e,{allowlist:t=[/./],denylist:a=[]}={}){super(i=>this._match(i),e),this._allowlist=t,this._denylist=a}_match({url:e,request:t}){if(t&&t.mode!=="navigate")return!1;const a=e.pathname+e.search;for(const i of this._denylist)if(i.test(a))return!1;return!!this._allowlist.some(i=>i.test(a))}}const ue={cacheWillUpdate:async({response:s})=>s.status===200||s.status===0?s:null};class fe extends k{constructor(e={}){super(e),this.plugins.some(t=>"cacheWillUpdate"in t)||this.plugins.unshift(ue),this._networkTimeoutSeconds=e.networkTimeoutSeconds||0}async _handle(e,t){const a=[],i=[];let n;if(this._networkTimeoutSeconds){const{id:o,promise:d}=this._getTimeoutPromise({request:e,logs:a,handler:t});n=o,i.push(d)}const r=this._getNetworkPromise({timeoutId:n,request:e,logs:a,handler:t});i.push(r);const c=await t.waitUntil((async()=>await t.waitUntil(Promise.race(i))||await r)());if(!c)throw new l("no-response",{url:e.url});return c}_getTimeoutPromise({request:e,logs:t,handler:a}){let i;return{promise:new Promise(r=>{i=setTimeout(async()=>{r(await a.cacheMatch(e))},this._networkTimeoutSeconds*1e3)}),id:i}}async _getNetworkPromise({timeoutId:e,request:t,logs:a,handler:i}){let n,r;try{r=await i.fetchAndCachePut(t)}catch(c){c instanceof Error&&(n=c)}return e&&clearTimeout(e),(n||!r)&&(r=await i.cacheMatch(t)),r}}class he extends k{constructor(e={}){super(e),this._networkTimeoutSeconds=e.networkTimeoutSeconds||0}async _handle(e,t){let a,i;try{const n=[t.fetch(e)];if(this._networkTimeoutSeconds){const r=T(this._networkTimeoutSeconds*1e3);n.push(r)}if(i=await Promise.race(n),!i)throw new Error(`Timed out the network response after ${this._networkTimeoutSeconds} seconds.`)}catch(n){n instanceof Error&&(a=n)}if(!i)throw new l("no-response",{url:e.url,error:a});return i}}importScripts("sw-runtime-resources-precache.js");self.skipWaiting();z();let A=[{url:".",revision:"d65238a83e94af715f533c75fba6208d"},{url:"sw.js",revision:"60dacfac6a09fe0057194cf53da65f99"},{url:"VAADIN/build/FlowBootstrap-feff2646.js",revision:"86c7b60228bd60b898bd22f12bb25dd6"},{url:"VAADIN/build/FlowClient-341d667e.js",revision:"5b9fbf60cc8cc0bc2e4a23f47d1898d0"},{url:"VAADIN/build/generated-flow-imports-82f22cab.js",revision:"1366dd679d70212f9109d3ac5c69d08e"},{url:"VAADIN/build/indexhtml-5d97bb51.js",revision:"d084b389822b6ad47830adabea7f0916"},{url:"VAADIN/build/vaadin-accordion-eed3b794-2fa781a3.js",revision:"33eb89d25fbda132a16b73a8deb4fa84"},{url:"VAADIN/build/vaadin-accordion-heading-c0acdd6d-8533b9de.js",revision:"bed4802c1cb3f6b83b411009d4f51d24"},{url:"VAADIN/build/vaadin-accordion-panel-616e55d6-1e2d3e96.js",revision:"2b31d9bdd1c3e7a9fca62152e148f8b7"},{url:"VAADIN/build/vaadin-app-layout-e56de2e9-60ff604e.js",revision:"2bc50774fb3bf227a526e32cf8742e86"},{url:"VAADIN/build/vaadin-avatar-7599297d-9025ac60.js",revision:"f0fc991cac3b1f9e5803bf26a8bbd499"},{url:"VAADIN/build/vaadin-big-decimal-field-e51def24-72ee517d.js",revision:"2f908fb373690db555256eb277753d07"},{url:"VAADIN/build/vaadin-board-828ebdea-316074c1.js",revision:"b1f45aaf4efc5fb7ff752f5e9a6f8540"},{url:"VAADIN/build/vaadin-board-row-c70d0c55-89ea2a51.js",revision:"94c79db3fa93a196c867104ed32c3835"},{url:"VAADIN/build/vaadin-button-2511ad84-e7cff8b0.js",revision:"6af2c65c959646f0dd2b2033896a3737"},{url:"VAADIN/build/vaadin-chart-5192dc15-f54ccf10.js",revision:"679e4cc5b5e1da3cf9d5c7d17d484229"},{url:"VAADIN/build/vaadin-checkbox-4e68df64-31263686.js",revision:"d8e7ae11283526e068daea0904af93d6"},{url:"VAADIN/build/vaadin-checkbox-group-a7c65bf2-4d6f03ec.js",revision:"4f1cba6135e5d329b915dc66c8574664"},{url:"VAADIN/build/vaadin-combo-box-96451ddd-f9b00ce8.js",revision:"a6b0092ee53f093c02735378ab91f849"},{url:"VAADIN/build/vaadin-confirm-dialog-4d718829-0ecb1cee.js",revision:"1018f6950c75b55e3756fb1d15c559e7"},{url:"VAADIN/build/vaadin-cookie-consent-46c09f8b-0b47b9f8.js",revision:"880a0f27c8bcc7ef8a0d535d9f029294"},{url:"VAADIN/build/vaadin-crud-8d161a22-1c83ccf1.js",revision:"ac17ba602fae7f99c7957869f9b2eab9"},{url:"VAADIN/build/vaadin-custom-field-42c85b9e-63ce48d7.js",revision:"33f8a801edea5cbf0994eeaafc9425b7"},{url:"VAADIN/build/vaadin-date-picker-f2001167-a172c673.js",revision:"7e6dbf89ea126dd24d597f65accb8785"},{url:"VAADIN/build/vaadin-date-time-picker-c8c047a7-87aeb4a7.js",revision:"bcd2440b73a8ddc8647c18d9ff8be059"},{url:"VAADIN/build/vaadin-details-bf336660-57f5d917.js",revision:"7d8db2b67000975b9a447960a668a887"},{url:"VAADIN/build/vaadin-details-summary-351a1448-8792010e.js",revision:"2835638e56d015c1e9b219a7120c7e0c"},{url:"VAADIN/build/vaadin-dialog-53253a08-79182d24.js",revision:"81151b08586882cd95aaf09a307a2ee5"},{url:"VAADIN/build/vaadin-email-field-d7a35f04-ce1be9c6.js",revision:"e24d169f07a87e220302672eee45c888"},{url:"VAADIN/build/vaadin-form-layout-47744b1d-f0ca62c9.js",revision:"087e0419b2b4c9dcc42bf28051d2008a"},{url:"VAADIN/build/vaadin-grid-0a4791c2-df37b668.js",revision:"d75318ff3ef4e55a31573371e85566a4"},{url:"VAADIN/build/vaadin-grid-pro-ff415555-b18f4a5a.js",revision:"f3d03e461342c0670d690dd23c01dc44"},{url:"VAADIN/build/vaadin-horizontal-layout-3193943f-69843c6d.js",revision:"dd2858f662a29447ec4f48c9bae52ed2"},{url:"VAADIN/build/vaadin-icon-601f36ed-fa4b792e.js",revision:"6e8beb7e63a5c0e66db69fb282d68c44"},{url:"VAADIN/build/vaadin-integer-field-85078932-5907103a.js",revision:"fb5a6ff19b747846e8b0caf337ea4652"},{url:"VAADIN/build/vaadin-list-box-d7a8433b-27154ab5.js",revision:"836b51332bf341d4a469af78a155e8f3"},{url:"VAADIN/build/vaadin-login-form-638996c6-8ca19462.js",revision:"9a222413477140e2ff1fb96bc7e7bf3c"},{url:"VAADIN/build/vaadin-login-overlay-f8a5db8a-6083d712.js",revision:"a595aba772b78a25f762fa5c051392fe"},{url:"VAADIN/build/vaadin-map-d40a0116-c42ca2e4.js",revision:"1163a5c5a77de1c301b8ae1f3d58cf31"},{url:"VAADIN/build/vaadin-menu-bar-3f5ab096-3fcae007.js",revision:"509a3b323ef7dca66add0f61f3a9832d"},{url:"VAADIN/build/vaadin-message-input-996ac37c-5fca8a20.js",revision:"fa206e19dddf3c0182032daef9cd5367"},{url:"VAADIN/build/vaadin-message-list-70a435ba-e9e45e23.js",revision:"3891f6672ecc140147eb6c544672a119"},{url:"VAADIN/build/vaadin-multi-select-combo-box-a3373557-c34da4bf.js",revision:"8991fa682ef6451b434d5e91ccc44d96"},{url:"VAADIN/build/vaadin-notification-bd6eb776-aad3f546.js",revision:"b43c35e9b3c5b1f7af3f80f46b47921b"},{url:"VAADIN/build/vaadin-number-field-cb3ee8b2-efb1ba98.js",revision:"31c62184fea6cfba53dd63a9233bea92"},{url:"VAADIN/build/vaadin-password-field-d289cb18-838d7953.js",revision:"ba5a67773cb19af538eba322f1e373eb"},{url:"VAADIN/build/vaadin-progress-bar-309ecf1f-ab42e939.js",revision:"4ee78874730b82f5ae0e7386020a5c3f"},{url:"VAADIN/build/vaadin-radio-group-88b5afd8-84678a23.js",revision:"2943424822412f097af093dc09cd4fe3"},{url:"VAADIN/build/vaadin-rich-text-editor-8cd892f2-5523fb35.js",revision:"b6d7c8ba4ef588d42cdb21b55f41e59b"},{url:"VAADIN/build/vaadin-scroller-35e68818-16ee62f9.js",revision:"983d283a40ef75cec02f40617be087a6"},{url:"VAADIN/build/vaadin-select-df6e9947-30d255d3.js",revision:"2340882a0d57d957f57a069e0f9bbe42"},{url:"VAADIN/build/vaadin-side-nav-ba80d91d-989a1a9c.js",revision:"f67a845b2a237762396d0d6a25f5a4da"},{url:"VAADIN/build/vaadin-side-nav-item-34918f92-46fb2b1b.js",revision:"a93034679f3159ccaf7e6cbcc613dad3"},{url:"VAADIN/build/vaadin-split-layout-80c92131-93b99ec0.js",revision:"35a50954fe3b5749297aa6afa220a085"},{url:"VAADIN/build/vaadin-spreadsheet-59d8c5ef-62fb20e6.js",revision:"5369dfc5fe90d8b577457baa884ea1da"},{url:"VAADIN/build/vaadin-tab-aaf32809-7ed545ae.js",revision:"b7d120436ab9dd9b72fe6213c48d900f"},{url:"VAADIN/build/vaadin-tabs-d9a5e24e-234c74e0.js",revision:"2bf160c45e74ad1931a5fda6242c08c2"},{url:"VAADIN/build/vaadin-tabsheet-dd99ed9a-9ac62389.js",revision:"8c8799d1998605190bc2465c2d624ed6"},{url:"VAADIN/build/vaadin-text-area-83627ebc-1ea7631c.js",revision:"056bc21587a0d24becf848c8c59e4f85"},{url:"VAADIN/build/vaadin-text-field-0b3db014-af5c7d8c.js",revision:"0562fa27c6ef78dcb57e18e1a11feccb"},{url:"VAADIN/build/vaadin-time-picker-715ec415-c142b4eb.js",revision:"9bc7a871540ea4940cde57a189c8627a"},{url:"VAADIN/build/vaadin-upload-d3c162ed-13e61497.js",revision:"1d2adc2c821a287dfcfe7c7f7e65ac5b"},{url:"VAADIN/build/vaadin-vertical-layout-ad4174c4-c10e5bc5.js",revision:"72a6fbd2f8cd2a73e912ebd833e86c95"},{url:"VAADIN/build/vaadin-virtual-list-96896203-e164d7ee.js",revision:"ace759be17346311a5fc578f150dd5c1"}],be=A.findIndex(s=>s.url===".")>=0;var V;(V=self.additionalManifestEntries)!=null&&V.length&&A.push(...self.additionalManifestEntries.filter(s=>s.url!=="."||!be));const pe=".",ge=new URL(self.registration.scope);async function we(s){const e=await s.text();return new Response(e.replace(/<base\s+href=[^>]*>/,`<base href="${self.registration.scope}">`),s)}function ye(s){return A.some(e=>L(e.url)===L(`${s}`))}let N=!1;function E(){return{async fetchDidFail(){N=!0},async fetchDidSucceed({response:s}){return N=!1,s}}}const me=new he({plugins:[E()]});new fe({plugins:[E()]});x(new de(async s=>{async function e(){const a=await P(pe);return a?we(a):void 0}function t(){return s.url.pathname===ge.pathname?e():ye(s.url)?P(s.request):e()}if(!self.navigator.onLine){const a=await t();if(a)return a}try{return await me.handle(s)}catch(a){const i=await t();if(i)return i;throw a}}));le(A);self.addEventListener("message",s=>{var e;typeof s.data!="object"||!("method"in s.data)||s.data.method==="Vaadin.ServiceWorker.isConnectionLost"&&"id"in s.data&&((e=s.source)==null||e.postMessage({id:s.data.id,result:N},[]))});self.addEventListener("push",s=>{var t;const e=(t=s.data)==null?void 0:t.json();e&&self.registration.showNotification(e.title,{body:e.body})});self.addEventListener("notificationclick",s=>{s.notification.close(),s.waitUntil(ve())});async function ve(){const s=new URL("/",self.location.origin).href,t=(await self.clients.matchAll({type:"window"})).find(a=>a.url===s);return t?t.focus():self.clients.openWindow(s)}
