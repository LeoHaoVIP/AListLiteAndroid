(function(){System.register([`./store-legacy-DxhJyMhP.js`,`./tb-legacy-SSHDkT7P.js`,`./media_api-legacy-Bt6D6bWj.js`,`./OpenWithPlayer-legacy-B0vnlQZI.js`],function(e,t){var n,r,i,a,o,s,c,l,u,d,f,p,m,h,g,_,v,y,b,x,S,C,w,T,E,D,O,k,A,j,M,N,P,F,I,L;return{setters:[function(e){n=e.Aa,r=e.Dn,i=e.Gi,a=e.Xi,o=e.Yi,s=e.Zi,c=e.aa,l=e.ba,u=e.bt,d=e.ca,f=e.da,p=e.ea,m=e.ga,h=e.ia,g=e.ka,_=e.kn,v=e.vi,y=e.wa,b=e.xa},function(e){x=e.S,S=e.V,C=e._,w=e.h,T=e.m,E=e.p},function(e){D=e.J,O=e.U},function(e){k=e.a,A=e.n,j=e.r}],execute:function(){M=h(`<div style="position:absolute;top:calc(100% + 6px);left:0;z-index:1200;min-width:200px;max-height:340px;overflow-y:auto;padding:5px;border-radius:10px;box-shadow:0 12px 32px rgba(0,0,0,0.35)">`),N=h(`<div style=position:relative;display:inline-flex><style>
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
      </style><div style=display:inline-flex;align-items:stretch;border-radius:8px;overflow:hidden;box-shadow:none><button class="media-split-btn media-split-btn--main"></button><button class="media-split-btn media-split-btn--caret"aria-label=选择播放器 title=选择播放器>`),P=h(`<span style=font-size:10px;opacity:0.8>（不支持）`),F=h(`<button style="width:100%;display:flex;align-items:center;gap:8px;padding:8px 10px;border:none;border-radius:7px;font-size:13px;text-align:left"><span style=flex:1>`),I=`media-play-target`,L=e=>{switch(e){case`browser`:return T;case`vlc`:return E;case`emby`:return x;default:return S}},e(`t`,e=>{let{colorMode:t}=v(),o=l(()=>t()===`dark`),h=()=>A(u()),x=(()=>{try{return localStorage.getItem(I)??`browser`}catch{return`browser`}})(),S=()=>[{id:`browser`,name:`浏览器播放`,browser:!0,disabled:!!e.browserUnsupported},...h().map(e=>({id:e.id,name:e.name,browser:!1,disabled:!1}))],[T,E]=y((e=>S().some(t=>t.id===e&&!t.disabled))(x)?x:S().find(e=>!e.browser&&!e.disabled)?.id??(e.browserUnsupported?``:`browser`)),[R,z]=y(!1),[B,V]=y(null),[H,U]=y(null),W=null,G,K=()=>h().find(e=>e.id===T()),q=()=>T()?T()===`browser`?e.browserUnsupported?`选择播放器`:`浏览器播放`:`播放器播放`:`选择播放器`,J=async()=>{if(e.path){let t=await O({path:e.path});return t.code!==200||!t.data?(r.error(t.message||`获取直链失败`),null):D(t.data.url)}if(!e.itemId)return r.error(`缺少媒体信息`),null;let t=await O({id:e.itemId,episode:e.episode});return t.code!==200||!t.data?(r.error(t.message||`获取直链失败`),null):D(t.data.url)},Y=async()=>{if(H())return H();try{let e=await(await fetch(`${_}/@emby/System/Info/Public`)).json();if(e?.Id)return U(String(e.Id)),String(e.Id)}catch{}return null},X=()=>{T()===`browser`||W||(W=T()===`emby`?Y().finally(()=>{W=null}):J().then(e=>(e&&V(e),e)).finally(()=>{W=null}))},Z=(t,n,r,i)=>{window.location.href=j(t,{durl:n,name:e.name??`media`,serverId:r,itemId:i})},Q=async()=>{if(T()===`browser`){if(e.browserUnsupported){r.error(e.unsupportedHint||`该文件无法在浏览器内播放，请选择本地播放器`),z(!0);return}e.onBrowserPlay();return}let t=K();if(!t){if(e.browserUnsupported){r.error(e.unsupportedHint||`没有可用的本地播放器，请先下载该文件`),z(!0);return}e.onBrowserPlay();return}if(t.id===`emby`){if(!e.itemId){r.error(`缺少媒体信息`);return}let n=H();if(n){Z(t.scheme,``,n,e.itemId);return}let i=await Y();if(!i){r.error(`无法获取 Emby 服务器 ID`);return}Z(t.scheme,``,i,e.itemId);return}let n=B();if(n){Z(t.scheme,n);return}let i=await J();i&&(V(i),Z(t.scheme,i))},$=t=>{let n=S().find(e=>e.id===t);if(!n||n.disabled)r.error(e.unsupportedHint||`该文件无法在浏览器内播放，请选择本地播放器`);else{E(t),V(null),z(!1);try{localStorage.setItem(I,t)}catch{}}};return n(()=>{let e=e=>{G&&!G.contains(e.target)&&z(!1)};document.addEventListener(`click`,e),g(()=>document.removeEventListener(`click`,e))}),(()=>{var e=N(),t=e.firstChild.nextSibling,n=t.firstChild,r=n.nextSibling,l=G;return typeof l==`function`?c(l,e):G=e,n.addEventListener(`focus`,X),n.addEventListener(`mouseenter`,X),n.$$click=Q,a(n,m(i,{get component(){return L(T())},size:17}),null),a(n,q,null),r.$$click=e=>{e.stopPropagation(),z(!R())},a(r,m(C,{size:15})),a(e,m(f,{get when(){return R()},get children(){var e=M();return a(e,m(d,{get each(){return S()},children:e=>(()=>{var t=F(),n=t.firstChild;return t.$$click=()=>$(e.id),a(t,m(i,{get component(){return L(e.id)},size:15}),n),a(n,()=>e.name),a(t,m(f,{get when(){return e.disabled},get children(){return P()}}),null),a(t,m(f,{get when(){return s(()=>T()===e.id)()&&!e.disabled},get children(){return m(w,{size:14})}}),null),b(n=>{var r=T()===e.id?o()?`rgba(255,255,255,0.08)`:`rgba(0,0,0,0.05)`:`transparent`,i=o()?`#e2e8f0`:`#1e293b`,a=e.disabled?`not-allowed`:`pointer`,s=e.disabled?.45:1;return r!==n.e&&p(t,`background`,n.e=r),i!==n.t&&p(t,`color`,n.t=i),a!==n.a&&p(t,`cursor`,n.a=a),s!==n.o&&p(t,`opacity`,n.o=s),n},{e:void 0,t:void 0,a:void 0,o:void 0}),t})()})),b(t=>{var n=o()?`rgba(20,26,40,0.98)`:`#fff`,r=`1px solid ${o()?`rgba(255,255,255,0.1)`:`rgba(0,0,0,0.08)`}`;return n!==t.e&&p(e,`background`,t.e=n),r!==t.t&&p(e,`border`,t.t=r),t},{e:void 0,t:void 0}),e}}),null),b(e=>p(t,`background`,k())),e})()}),o([`click`])}}})})();