import{Aa as e,Dn as t,Gi as n,Xi as r,Yi as i,Zi as a,aa as o,ba as s,bt as c,ca as l,da as u,ea as d,ga as f,ia as p,ka as m,kn as h,vi as g,wa as _,xa as v}from"./store-BFZZVF2T.js";import{S as y,V as b,_ as x,h as S,m as C,p as w}from"./tb-BOpT-CJr.js";import{J as T,U as E}from"./media_api-CWvF4HdX.js";import{a as D,n as O,r as k}from"./OpenWithPlayer-0yKz-SxK.js";var A=p(`<div style="position:absolute;top:calc(100% + 6px);left:0;z-index:1200;min-width:200px;max-height:340px;overflow-y:auto;padding:5px;border-radius:10px;box-shadow:0 12px 32px rgba(0,0,0,0.35)">`),j=p(`<div style=position:relative;display:inline-flex><style>
        .media-split-btn {
          appearance: none;
          -webkit-appearance: none;
          border: 0;
          margin: 0;
          background: transparent;
          color: #fff;
          font-family: inherit;
          font-size: 14px;
          font-weight: 600;
          line-height: 1.2;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          box-shadow: none;
          outline: none;
        }
        .media-split-btn:hover,
        .media-split-btn:focus,
        .media-split-btn:active {
          background: rgba(255, 255, 255, 0.1);
          box-shadow: none;
          outline: none;
        }
        .media-split-btn--main {
          gap: 8px;
          padding: 10px 18px;
        }
        .media-split-btn--caret {
          width: 26px;
          flex: 0 0 26px;
          padding: 0;
          border-left: 1px solid rgba(255, 255, 255, 0.22);
        }
      </style><div style=display:inline-flex;align-items:stretch;border-radius:8px;overflow:hidden;box-shadow:none><button class="media-split-btn media-split-btn--main"></button><button class="media-split-btn media-split-btn--caret"aria-label=选择播放器 title=选择播放器>`),M=p(`<span style=font-size:10px;opacity:0.8>（不支持）`),N=p(`<button style="width:100%;display:flex;align-items:center;gap:8px;padding:8px 10px;border:none;border-radius:7px;font-size:13px;text-align:left"><span style=flex:1>`),P=`media-play-target`,F=e=>{switch(e){case`browser`:return C;case`vlc`:return w;case`emby`:return y;default:return b}},I=i=>{let{colorMode:p}=g(),y=s(()=>p()===`dark`),b=()=>O(c()),C=(()=>{try{return localStorage.getItem(P)??`browser`}catch{return`browser`}})(),w=()=>[{id:`browser`,name:`浏览器播放`,browser:!0,disabled:!!i.browserUnsupported},...b().map(e=>({id:e.id,name:e.name,browser:!1,disabled:!1}))],[I,L]=_((e=>w().some(t=>t.id===e&&!t.disabled))(C)?C:w().find(e=>!e.browser&&!e.disabled)?.id??(i.browserUnsupported?``:`browser`)),[R,z]=_(!1),[B,V]=_(null),[H,U]=_(null),W=null,G,K=()=>b().find(e=>e.id===I()),q=()=>I()?I()===`browser`?i.browserUnsupported?`选择播放器`:`浏览器播放`:`播放器播放`:`选择播放器`,J=async()=>{if(i.path){let e=await E({path:i.path});return e.code!==200||!e.data?(t.error(e.message||`获取直链失败`),null):T(e.data.url)}if(!i.itemId)return t.error(`缺少媒体信息`),null;let e=await E({id:i.itemId,episode:i.episode});return e.code!==200||!e.data?(t.error(e.message||`获取直链失败`),null):T(e.data.url)},Y=async()=>{if(H())return H();try{let e=await(await fetch(`${h}/@emby/System/Info/Public`)).json();if(e?.Id)return U(String(e.Id)),String(e.Id)}catch{}return null},X=()=>{I()===`browser`||W||(W=I()===`emby`?Y().finally(()=>{W=null}):J().then(e=>(e&&V(e),e)).finally(()=>{W=null}))},Z=(e,t,n,r)=>{window.location.href=k(e,{durl:t,name:i.name??`media`,serverId:n,itemId:r})},Q=async()=>{if(I()===`browser`){if(i.browserUnsupported){t.error(i.unsupportedHint||`该文件无法在浏览器内播放，请选择本地播放器`),z(!0);return}i.onBrowserPlay();return}let e=K();if(!e){if(i.browserUnsupported){t.error(i.unsupportedHint||`没有可用的本地播放器，请先下载该文件`),z(!0);return}i.onBrowserPlay();return}if(e.id===`emby`){if(!i.itemId){t.error(`缺少媒体信息`);return}let n=H();if(n){Z(e.scheme,``,n,i.itemId);return}let r=await Y();if(!r){t.error(`无法获取 Emby 服务器 ID`);return}Z(e.scheme,``,r,i.itemId);return}let n=B();if(n){Z(e.scheme,n);return}let r=await J();r&&(V(r),Z(e.scheme,r))},$=e=>{let n=w().find(t=>t.id===e);if(!n||n.disabled)t.error(i.unsupportedHint||`该文件无法在浏览器内播放，请选择本地播放器`);else{L(e),V(null),z(!1);try{localStorage.setItem(P,e)}catch{}}};return e(()=>{let e=e=>{G&&!G.contains(e.target)&&z(!1)};document.addEventListener(`click`,e),m(()=>document.removeEventListener(`click`,e))}),(()=>{var e=j(),t=e.firstChild.nextSibling,i=t.firstChild,s=i.nextSibling,c=G;return typeof c==`function`?o(c,e):G=e,i.addEventListener(`focus`,X),i.addEventListener(`mouseenter`,X),i.$$click=Q,r(i,f(n,{get component(){return F(I())},size:17}),null),r(i,q,null),s.$$click=e=>{e.stopPropagation(),z(!R())},r(s,f(x,{size:15})),r(e,f(u,{get when(){return R()},get children(){var e=A();return r(e,f(l,{get each(){return w()},children:e=>(()=>{var t=N(),i=t.firstChild;return t.$$click=()=>$(e.id),r(t,f(n,{get component(){return F(e.id)},size:15}),i),r(i,()=>e.name),r(t,f(u,{get when(){return e.disabled},get children(){return M()}}),null),r(t,f(u,{get when(){return a(()=>I()===e.id)()&&!e.disabled},get children(){return f(S,{size:14})}}),null),v(n=>{var r=I()===e.id?y()?`rgba(255,255,255,0.08)`:`rgba(0,0,0,0.05)`:`transparent`,i=y()?`#e2e8f0`:`#1e293b`,a=e.disabled?`not-allowed`:`pointer`,o=e.disabled?.45:1;return r!==n.e&&d(t,`background`,n.e=r),i!==n.t&&d(t,`color`,n.t=i),a!==n.a&&d(t,`cursor`,n.a=a),o!==n.o&&d(t,`opacity`,n.o=o),n},{e:void 0,t:void 0,a:void 0,o:void 0}),t})()})),v(t=>{var n=y()?`rgba(20,26,40,0.98)`:`#fff`,r=`1px solid ${y()?`rgba(255,255,255,0.1)`:`rgba(0,0,0,0.08)`}`;return n!==t.e&&d(e,`background`,t.e=n),r!==t.t&&d(e,`border`,t.t=r),t},{e:void 0,t:void 0}),e}}),null),v(e=>d(t,`background`,D())),e})()};i([`click`]);export{I as t};