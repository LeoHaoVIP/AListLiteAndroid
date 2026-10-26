import{Ca as e,Ea as t,Ma as n,Qi as r,Sa as i,St as a,Zi as o,bi as s,ja as c,jn as l,kn as u,na as d,oa as f,pa as p,qi as m,sa as h,ua as g,va as _}from"./store-BxFvGRq4.js";import{R as v,S as y,_ as b,h as x,m as S,p as C}from"./tb-RbDNyT4E.js";import{I as w}from"./media_api-CTvP5iMt.js";import{a as T,n as E,r as D}from"./OpenWithPlayer-CgSqwYRr.js";var O=f(`<div style="position:absolute;top:calc(100% + 6px);left:0;z-index:1200;min-width:200px;max-height:340px;overflow-y:auto;padding:5px;border-radius:10px;box-shadow:0 12px 32px rgba(0,0,0,0.35)">`),k=f(`<div style=position:relative;display:inline-flex><style>
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
      </style><div style=display:inline-flex;align-items:stretch;border-radius:8px;overflow:hidden;box-shadow:none><button class="media-split-btn media-split-btn--main"></button><button class="media-split-btn media-split-btn--caret"aria-label=选择播放器 title=选择播放器>`),A=f(`<button style="width:100%;display:flex;align-items:center;gap:8px;padding:8px 10px;border:none;border-radius:7px;font-size:13px;cursor:pointer;text-align:left"><span style=flex:1>`),j=`media-play-target`,M=e=>{switch(e){case`browser`:return S;case`vlc`:return C;case`emby`:return y;default:return v}},N=o=>{let{colorMode:f}=s(),v=i(()=>f()===`dark`),y=()=>E(a()),S=(()=>{try{return localStorage.getItem(j)??`browser`}catch{return`browser`}})(),[C,N]=t((e=>e===`browser`||y().some(t=>t.id===e))(S)?S:`browser`),[P,F]=t(!1),[I,L]=t(null),[R,z]=t(null),B=null,V,H=()=>y().find(e=>e.id===C()),U=()=>C()===`browser`?`浏览器播放`:`播放器播放`,W=async()=>{if(o.path){let e=await w({path:o.path});return e.code!==200||!e.data?(u.error(e.message||`获取直链失败`),null):e.data.url}if(!o.itemId)return u.error(`缺少媒体信息`),null;let e=await w({id:o.itemId,episode:o.episode});return e.code!==200||!e.data?(u.error(e.message||`获取直链失败`),null):e.data.url},G=async()=>{if(R())return R();try{let e=await(await fetch(`${l}/@emby/System/Info/Public`)).json();if(e?.Id)return z(String(e.Id)),String(e.Id)}catch{}return null},K=()=>{C()===`browser`||B||(B=C()===`emby`?G().finally(()=>{B=null}):W().then(e=>(e&&L(e),e)).finally(()=>{B=null}))},q=(e,t,n,r)=>{window.location.href=D(e,{durl:t,name:o.name??`media`,serverId:n,itemId:r})},J=async()=>{if(C()===`browser`){o.onBrowserPlay();return}let e=H();if(!e){o.onBrowserPlay();return}if(e.id===`emby`){if(!o.itemId){u.error(`缺少媒体信息`);return}let t=R();if(t){q(e.scheme,``,t,o.itemId);return}let n=await G();if(!n){u.error(`无法获取 Emby 服务器 ID`);return}q(e.scheme,``,n,o.itemId);return}let t=I();if(t){q(e.scheme,t);return}let n=await W();n&&(L(n),q(e.scheme,n))},Y=e=>{N(e),L(null),F(!1);try{localStorage.setItem(j,e)}catch{}};return n(()=>{let e=e=>{V&&!V.contains(e.target)&&F(!1)};document.addEventListener(`click`,e),c(()=>document.removeEventListener(`click`,e))}),(()=>{var t=k(),n=t.firstChild.nextSibling,i=n.firstChild,a=i.nextSibling,o=V;return typeof o==`function`?h(o,t):V=t,i.addEventListener(`focus`,K),i.addEventListener(`mouseenter`,K),i.$$click=J,r(i,_(m,{get component(){return M(C())},size:17}),null),r(i,U,null),a.$$click=e=>{e.stopPropagation(),F(!P())},r(a,_(b,{size:15})),r(t,_(p,{get when(){return P()},get children(){var t=O();return r(t,_(g,{get each(){return[{id:`browser`,name:`浏览器播放`},...y().map(e=>({id:e.id,name:e.name}))]},children:t=>(()=>{var n=A(),i=n.firstChild;return n.$$click=()=>Y(t.id),r(n,_(m,{get component(){return M(t.id)},size:15}),i),r(i,()=>t.name),r(n,_(p,{get when(){return C()===t.id},get children(){return _(x,{size:14})}}),null),e(e=>{var r=C()===t.id?v()?`rgba(255,255,255,0.08)`:`rgba(0,0,0,0.05)`:`transparent`,i=v()?`#e2e8f0`:`#1e293b`;return r!==e.e&&d(n,`background`,e.e=r),i!==e.t&&d(n,`color`,e.t=i),e},{e:void 0,t:void 0}),n})()})),e(e=>{var n=v()?`rgba(20,26,40,0.98)`:`#fff`,r=`1px solid ${v()?`rgba(255,255,255,0.1)`:`rgba(0,0,0,0.08)`}`;return n!==e.e&&d(t,`background`,e.e=n),r!==e.t&&d(t,`border`,e.t=r),e},{e:void 0,t:void 0}),t}}),null),e(e=>d(n,`background`,T())),t})()};o([`click`]);export{N as t};