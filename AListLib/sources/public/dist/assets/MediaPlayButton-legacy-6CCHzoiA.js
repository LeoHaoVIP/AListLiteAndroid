(function(){System.register([`./store-legacy-CrYc5qTi.js`,`./tb-legacy-BQoes3ge.js`,`./media_api-legacy-U_Bje01o.js`,`./OpenWithPlayer-legacy-d1vNMuoe.js`],function(e,t){var n,r,i,a,o,s,c,l,u,d,f,p,m,h,g,_,v,y,b,x,S,C,w,T,E,D,O,k,A,j,M,N,P;return{setters:[function(e){n=e.Ca,r=e.Ea,i=e.Ma,a=e.Qi,o=e.Sa,s=e.St,c=e.Zi,l=e.bi,u=e.ja,d=e.jn,f=e.kn,p=e.na,m=e.oa,h=e.pa,g=e.qi,_=e.sa,v=e.ua,y=e.va},function(e){b=e.R,x=e.S,S=e._,C=e.h,w=e.m,T=e.p},function(e){E=e.I},function(e){D=e.a,O=e.n,k=e.r}],execute:function(){A=m(`<div style="position:absolute;top:calc(100% + 6px);left:0;z-index:1200;min-width:200px;max-height:340px;overflow-y:auto;padding:5px;border-radius:10px;box-shadow:0 12px 32px rgba(0,0,0,0.35)">`),j=m(`<div style=position:relative;display:inline-flex><style>
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
      </style><div style=display:inline-flex;align-items:stretch;border-radius:8px;overflow:hidden;box-shadow:none><button class="media-split-btn media-split-btn--main"></button><button class="media-split-btn media-split-btn--caret"aria-label=选择播放器 title=选择播放器>`),M=m(`<button style="width:100%;display:flex;align-items:center;gap:8px;padding:8px 10px;border:none;border-radius:7px;font-size:13px;cursor:pointer;text-align:left"><span style=flex:1>`),N=`media-play-target`,P=e=>{switch(e){case`browser`:return w;case`vlc`:return T;case`emby`:return x;default:return b}},e(`t`,e=>{let{colorMode:t}=l(),c=o(()=>t()===`dark`),m=()=>O(s()),b=(()=>{try{return localStorage.getItem(N)??`browser`}catch{return`browser`}})(),[x,w]=r((e=>e===`browser`||m().some(t=>t.id===e))(b)?b:`browser`),[T,F]=r(!1),[I,L]=r(null),[R,z]=r(null),B=null,V,H=()=>m().find(e=>e.id===x()),U=()=>x()===`browser`?`浏览器播放`:`播放器播放`,W=async()=>{if(e.path){let t=await E({path:e.path});return t.code!==200||!t.data?(f.error(t.message||`获取直链失败`),null):t.data.url}if(!e.itemId)return f.error(`缺少媒体信息`),null;let t=await E({id:e.itemId,episode:e.episode});return t.code!==200||!t.data?(f.error(t.message||`获取直链失败`),null):t.data.url},G=async()=>{if(R())return R();try{let e=await(await fetch(`${d}/@emby/System/Info/Public`)).json();if(e?.Id)return z(String(e.Id)),String(e.Id)}catch{}return null},K=()=>{x()===`browser`||B||(B=x()===`emby`?G().finally(()=>{B=null}):W().then(e=>(e&&L(e),e)).finally(()=>{B=null}))},q=(t,n,r,i)=>{window.location.href=k(t,{durl:n,name:e.name??`media`,serverId:r,itemId:i})},J=async()=>{if(x()===`browser`){e.onBrowserPlay();return}let t=H();if(!t){e.onBrowserPlay();return}if(t.id===`emby`){if(!e.itemId){f.error(`缺少媒体信息`);return}let n=R();if(n){q(t.scheme,``,n,e.itemId);return}let r=await G();if(!r){f.error(`无法获取 Emby 服务器 ID`);return}q(t.scheme,``,r,e.itemId);return}let n=I();if(n){q(t.scheme,n);return}let r=await W();r&&(L(r),q(t.scheme,r))},Y=e=>{w(e),L(null),F(!1);try{localStorage.setItem(N,e)}catch{}};return i(()=>{let e=e=>{V&&!V.contains(e.target)&&F(!1)};document.addEventListener(`click`,e),u(()=>document.removeEventListener(`click`,e))}),(()=>{var e=j(),t=e.firstChild.nextSibling,r=t.firstChild,i=r.nextSibling,o=V;return typeof o==`function`?_(o,e):V=e,r.addEventListener(`focus`,K),r.addEventListener(`mouseenter`,K),r.$$click=J,a(r,y(g,{get component(){return P(x())},size:17}),null),a(r,U,null),i.$$click=e=>{e.stopPropagation(),F(!T())},a(i,y(S,{size:15})),a(e,y(h,{get when(){return T()},get children(){var e=A();return a(e,y(v,{get each(){return[{id:`browser`,name:`浏览器播放`},...m().map(e=>({id:e.id,name:e.name}))]},children:e=>(()=>{var t=M(),r=t.firstChild;return t.$$click=()=>Y(e.id),a(t,y(g,{get component(){return P(e.id)},size:15}),r),a(r,()=>e.name),a(t,y(h,{get when(){return x()===e.id},get children(){return y(C,{size:14})}}),null),n(n=>{var r=x()===e.id?c()?`rgba(255,255,255,0.08)`:`rgba(0,0,0,0.05)`:`transparent`,i=c()?`#e2e8f0`:`#1e293b`;return r!==n.e&&p(t,`background`,n.e=r),i!==n.t&&p(t,`color`,n.t=i),n},{e:void 0,t:void 0}),t})()})),n(t=>{var n=c()?`rgba(20,26,40,0.98)`:`#fff`,r=`1px solid ${c()?`rgba(255,255,255,0.1)`:`rgba(0,0,0,0.08)`}`;return n!==t.e&&p(e,`background`,t.e=n),r!==t.t&&p(e,`border`,t.t=r),t},{e:void 0,t:void 0}),e}}),null),n(e=>p(t,`background`,D())),e})()}),c([`click`])}}})})();