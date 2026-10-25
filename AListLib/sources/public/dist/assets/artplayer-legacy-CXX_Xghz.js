(function(){System.register([`./rolldown-runtime-legacy-_agH4cHU.js`,`./store-legacy-BSWt4atL.js`],function(e,t){var n,r,i,a,o,s,c,l,u,d,f,p,m,h,g,_,v,y,b,x,S,C,w,T,E,D,O,k,A,j,M;function N(e){switch(e){case 1:case 2:case 3:return 0;case 4:return 2;case 5:return 1;default:return 0}}function P(e){if(typeof e!=`string`)return[];let t=e.matchAll(/<d[^>]*?p="(?<p>[^"]+)"[^>]*>(?<text>.*?)<\/d>/gs);return Array.from(t).map(e=>{let t=e.groups.p.split(`,`);return t.length>=8?{text:e.groups.text.trim().replaceAll(`&quot;`,`"`).replaceAll(`&apos;`,`'`).replaceAll(`&lt;`,`<`).replaceAll(`&gt;`,`>`).replaceAll(`&amp;`,`&`),time:Number(t[0]),mode:N(Number(t[1])),fontSize:Number(t[2]),color:`#${Number(t[3]).toString(16)}`,timestamp:Number(t[4]),pool:Number(t[5]),userID:t[6],rowID:Number(t[7])}:null}).filter(Boolean)}function F({data:e}){let{xml:t,id:n}=e;if(!n||!t)return;let r=P(t);globalThis.postMessage({danmus:r,id:n})}function I(){let e=`
        ${N.toString()}
        ${P.toString()}
        onmessage = ${F.toString()}
    `,t=new Blob([e],{type:`application/javascript`});return new Worker(URL.createObjectURL(t))}function L(e){return new Promise(async t=>{let n=await(await fetch(e)).text();try{let e=I();e.onmessage=n=>{let{danmus:r,id:i}=n.data;i&&r&&(t(r),e.terminate())},e.postMessage({xml:n,id:Date.now()})}catch(e){console.error(`Error parsing Bilibili Danmu:`,e),t(P(n))}})}function R(e){let t;try{if(t=s&&(self.URL||self.webkitURL).createObjectURL(s),!t)throw``;let n=new Worker(t,{type:`module`,name:e?.name});return n.addEventListener(`error`,()=>{(self.URL||self.webkitURL).revokeObjectURL(t)}),n}catch(t){return new Worker(`data:text/javascript;charset=utf-8,`+encodeURIComponent(o),{type:`module`,name:e?.name})}}function z(e,t){let n=t[0]-e[0],r=t[1]-e[1];return{length:Math.sqrt(n**2+r**2),angle:Math.atan2(r,n)}}function B(e,t,n){let{query:r}=e.constructor.utils;e.controls.add({name:`heatmap`,position:`top`,html:``,style:{position:`absolute`,top:`-100px`,left:`0px`,right:`0px`,height:`100px`,width:`100%`,pointerEvents:`none`},mounted(i){let a=null,o=null;function s(s=[]){if(a=null,o=null,i.innerHTML=``,!e.duration||e.option.isLive)return;let c={w:i.offsetWidth,h:i.offsetHeight},u={xMin:0,xMax:c.w,yMin:0,yMax:128,scale:.25,opacity:.2,minHeight:Math.floor(c.h*.05),sampling:Math.floor(c.w/100),smoothing:.2,flattening:.2};typeof n==`object`&&Object.assign(u,n);let d=[];if(Array.isArray(s)&&s.length)d=[...s];else{let n=e.duration/c.w;for(let e=0;e<=c.w;e+=u.sampling){let r=t.queue.filter(({time:t})=>t>e*n&&t<=(e+u.sampling)*n).length;d.push([e,r])}}if(d.length===0)return;let f=d[d.length-1],p=f[0],m=f[1];p!==c.w&&d.push([c.w,m]);let h=d.map(e=>e[1]),g=(Math.min(...h)+Math.max(...h))/2;for(let e=0;e<d.length;e++){let t=d[e],n=t[1];t[1]=n*(n>g?1+u.scale:1-u.scale)+u.minHeight}let _=(e,t,n,r)=>{let i=z(t||e,n||e),a=l.map(Math.cos(i.angle)*u.flattening,0,1,1,0),o=i.angle*a+(r?Math.PI:0),s=i.length*u.smoothing;return[e[0]+Math.cos(o)*s,e[1]+Math.sin(o)*s]},v=(e,t,n)=>{let r=_(n[t-1],n[t-2],e),i=_(e,n[t-1],n[t+1],!0),a=t===n.length-1?` z`:``;return`C ${r[0]},${r[1]} ${i[0]},${i[1]} ${e[0]},${e[1]}${a}`},y=d.map(e=>[l.map(e[0],u.xMin,u.xMax,0,c.w),l.map(e[1],u.yMin,u.yMax,c.h,0)]).reduce((e,t,n,r)=>n===0?`M ${r[r.length-1][0]},${c.h} L ${t[0]},${c.h} L ${t[0]},${t[1]}`:`${e} ${v(t,n,r)}`,``);i.innerHTML=`
                    <svg viewBox="0 0 ${c.w} ${c.h}">
                        <defs>
                            <linearGradient id="heatmap-solids" x1="0%" y1="0%" x2="100%" y2="0%">
                                <stop offset="0%" style="stop-color:var(--art-theme);stop-opacity:${u.opacity}" />
                                <stop offset="0%" style="stop-color:var(--art-theme);stop-opacity:${u.opacity}" id="heatmap-start" />
                                <stop offset="0%" style="stop-color:var(--art-progress-color);stop-opacity:1" id="heatmap-stop" />
                                <stop offset="100%" style="stop-color:var(--art-progress-color);stop-opacity:1" />
                            </linearGradient>
                        </defs>
                        <path fill="url(#heatmap-solids)" d="${y}"></path>
                    </svg>
                `,a=r(`#heatmap-start`,i),o=r(`#heatmap-stop`,i),a.setAttribute(`offset`,`${e.played*100}%`),o.setAttribute(`offset`,`${e.played*100}%`)}e.on(`video:timeupdate`,()=>{a&&o&&(a.setAttribute(`offset`,`${e.played*100}%`),o.setAttribute(`offset`,`${e.played*100}%`))}),e.on(`setBar`,(e,t)=>{a&&o&&e===`played`&&(a.setAttribute(`offset`,`${t*100}%`),o.setAttribute(`offset`,`${t*100}%`))}),e.on(`ready`,()=>s()),e.on(`resize`,()=>s()),e.on(`artplayerPluginDanmuku:loaded`,()=>s()),e.on(`artplayerPluginDanmuku:points`,e=>s(e))}})}function V(e){return t=>{let n=new c(t,e),r=new C(t,n);return n.option.heatmap&&B(t,n,n.option.heatmap),{name:`artplayerPluginDanmuku`,emit:n.emit.bind(n),load:n.load.bind(n),config:n.config.bind(n),hide:n.hide.bind(n),show:n.show.bind(n),reset:n.reset.bind(n),mount:r.mount.bind(r),get option(){return n.option},get isHide(){return n.isHide},get isStop(){return n.isStop}}}}function H(e){return/^https?:\/\//.test(e)}function U(e){return H(e)?e:new URL(e,document.baseURI).toString()}function W({workerUrl:e,wasmUrl:t}){return new Promise(n=>{fetch(e).then(e=>e.text()).then(r=>{let i=r;i=i.replace(/wasmBinaryFile\s*=\s*"(subtitles-octopus-worker\.wasm)"/g,(n,r)=>(t=t?U(t):new URL(r,U(e)).toString(),`wasmBinaryFile = "${t}"`));let a=new Blob([i],{type:`text/javascript`});n(URL.createObjectURL(a))})})}function G(e){M.canvasParent&&(M.canvasParent.style.display=e?`block`:`none`)}function K(e){return async t=>(M=new T.default({availableFonts:{"times new roman":U(A)},workerUrl:await W({workerUrl:O,wasmUrl:k}),fallbackFont:U(j),video:t.template.$video,...e}),M.canvasParent.className=`artplayer-plugin-ass`,M.canvasParent.style.cssText=`
      position: absolute;
      width: 100%;
      height: 100%;
      user-select: none;
      pointer-events: none;
      z-index: 20;
    `,t.on(`artplayer-plugin-ass:switch`,e=>{M.freeTrack(),M.setTrackByUrl(e),G(!0)}),t.on(`artplayer-plugin-ass:visible`,e=>G(e)),t.on(`subtitleOffset`,e=>M.timeOffset=e),t.on(`destroy`,()=>M.dispose()),{name:`artplayerPluginAss`,instance:M})}return e({n:V,t:K}),{setters:[function(e){n=e.i,r=e.t},function(e){i=e.B}],execute:function(){if(a=document.createElement(`style`),a.textContent=`@media (max-width:440px){.art-video-player .art-controls .art-control-previous-button,.art-video-player .art-controls .art-control-next-button,.art-video-player .art-controls .art-control-screenshot,.art-video-player .art-controls .art-control-time{display:none}}@media (max-width:600px){.art-video-player .art-controls .art-control-time{display:none}}
/*$vite$:1*/`,document.head.appendChild(a),o=`/*!
 * artplayer-plugin-danmuku.js v5.3.0
 * Github: https://github.com/zhw2590582/ArtPlayer
 * (c) 2017-2026 Harvey Zhao
 * Released under the MIT License.
 */
function getDanmuTop({ target, visibles, clientWidth, clientHeight, marginBottom, marginTop, antiOverlap }) {
  const maxTop = clientHeight - marginBottom;
  const danmus = visibles.filter((item) => item.mode === target.mode && item.top <= maxTop).sort((prev, next) => prev.top - next.top);
  if (danmus.length === 0) {
    if (target.mode === 2) {
      return maxTop - target.height;
    } else {
      return marginTop;
    }
  }
  danmus.unshift({
    type: "top",
    top: 0,
    left: 0,
    right: 0,
    height: marginTop,
    width: clientWidth,
    speed: 0,
    distance: clientWidth
  });
  danmus.push({
    type: "bottom",
    top: maxTop,
    left: 0,
    right: 0,
    height: marginBottom,
    width: clientWidth,
    speed: 0,
    distance: clientWidth
  });
  if (target.mode === 2) {
    for (let index = danmus.length - 2; index >= 0; index -= 1) {
      const item = danmus[index];
      const prev = danmus[index + 1];
      const itemBottom = item.top + item.height;
      const diff = prev.top - itemBottom;
      if (diff >= target.height) {
        return prev.top - target.height;
      }
    }
  } else {
    for (let index = 1; index < danmus.length; index += 1) {
      const item = danmus[index];
      const prev = danmus[index - 1];
      const prevBottom = prev.top + prev.height;
      const diff = item.top - prevBottom;
      if (diff >= target.height) {
        return prevBottom;
      }
    }
  }
  const topMap = [];
  for (let index = 1; index < danmus.length - 1; index += 1) {
    const item = danmus[index];
    if (topMap.length) {
      const last = topMap[topMap.length - 1];
      if (last[0].top === item.top) {
        last.push(item);
      } else {
        topMap.push([item]);
      }
    } else {
      topMap.push([item]);
    }
  }
  if (antiOverlap) {
    switch (target.mode) {
      case 0: {
        const result = topMap.find((list) => {
          return list.every((danmu) => {
            if (clientWidth < danmu.distance)
              return false;
            if (target.speed < danmu.speed)
              return true;
            const overlapTime = danmu.right / (target.speed - danmu.speed);
            if (overlapTime > danmu.time)
              return true;
            return false;
          });
        });
        return result && result[0] ? result[0].top : void 0;
      }
      // 静止弹幕没有重叠问题
      case 1:
      case 2:
        return void 0;
    }
  } else {
    switch (target.mode) {
      case 0:
        topMap.sort((prev, next) => {
          const nextMinRight = Math.min(...next.map((item) => item.right));
          const prevMinRight = Math.min(...prev.map((item) => item.right));
          return nextMinRight * next.length - prevMinRight * prev.length;
        });
        break;
      case 1:
      case 2:
        topMap.sort((prev, next) => {
          const nextMaxWidth = Math.max(...next.map((item) => item.width));
          const prevMaxWidth = Math.max(...prev.map((item) => item.width));
          return prevMaxWidth * prev.length - nextMaxWidth * next.length;
        });
        break;
    }
    return topMap[0][0].top;
  }
}
onmessage = (event) => {
  const { data } = event;
  if (!data.id || !data.type)
    return;
  const fns = { getDanmuTop };
  const fn = fns[data.type];
  const result = fn(data);
  globalThis.postMessage({
    result,
    id: data.id
  });
};
`,s=typeof self<`u`&&self.Blob&&new Blob([`URL.revokeObjectURL(import.meta.url);`,o],{type:`text/javascript;charset=utf-8`}),c=class e{constructor(t,n){let{constructor:r,template:i}=t;this.utils=r.utils,this.validator=r.validator,this.$danmuku=i.$danmuku,this.$player=i.$player,this.art=t,this.queue=[],this.$refs=[],this.isStop=!1,this.isHide=!1,this.timer=null,this.index=0,this.option=e.option,this.states={wait:[],ready:[],emit:[],stop:[]},this.config(n,!0),this.worker=new R,this.start=this.start.bind(this),this.stop=this.stop.bind(this),this.reset=this.reset.bind(this),this.resize=this.resize.bind(this),this.destroy=this.destroy.bind(this),t.on(`video:play`,this.start),t.on(`video:playing`,this.start),t.on(`video:pause`,this.stop),t.on(`video:waiting`,this.stop),t.on(`destroy`,this.destroy),t.on(`resize`,this.resize),this.load()}static get option(){return{danmuku:[],speed:5,margin:[10,`25%`],opacity:1,color:`#FFFFFF`,mode:0,modes:[0,1,2],fontSize:25,antiOverlap:!0,synchronousPlayback:!1,mount:void 0,heatmap:!1,width:512,points:[],filter:()=>!0,beforeEmit:()=>!0,beforeVisible:()=>!0,visible:!0,emitter:!0,maxLength:200,lockTime:5,theme:`dark`,OPACITY:{},FONT_SIZE:{},MARGIN:{},SPEED:{},COLOR:[]}}static get scheme(){return{danmuku:`array|function|string`,speed:`number`,margin:`array`,opacity:`number`,color:`string`,mode:`number`,modes:`array`,fontSize:`number|string`,antiOverlap:`boolean`,synchronousPlayback:`boolean`,mount:`?htmldivelement|string`,heatmap:`object|boolean`,width:`number`,points:`array`,filter:`function`,beforeEmit:`function`,beforeVisible:`function`,visible:`boolean`,emitter:`boolean`,maxLength:`number`,lockTime:`number`,theme:`string`,OPACITY:`object`,FONT_SIZE:`object`,MARGIN:`object`,SPEED:`object`,COLOR:`array`}}static get cssText(){return`
            user-select: none;
            position: absolute;
            white-space: pre;
            pointer-events: none;
            perspective: 500px;
            display: inline-block;
            will-change: transform;
            font-weight: normal;
            line-height: 1.125;
            visibility: hidden;
            font-family: SimHei, "Microsoft JhengHei", Arial, Helvetica, sans-serif;
            text-shadow: rgb(0, 0, 0) 1px 0px 1px, rgb(0, 0, 0) 0px 1px 1px, rgb(0, 0, 0) 0px -1px 1px, rgb(0, 0, 0) -1px 0px 1px;
        `}get isRotate(){return this.art.plugins?.autoOrientation?.state}get marginTop(){let{clamp:t}=this.utils,n=this.option.margin[0],{clientHeight:r}=this.$player;return typeof n==`number`?t(n,0,r):typeof n==`string`&&n.endsWith(`%`)?t(Number.parseFloat(n)/100*r,0,r):e.option.margin[0]}get marginBottom(){let{clamp:t}=this.utils,n=this.option.margin[1],{clientHeight:r}=this.$player;return typeof n==`number`?t(n,0,r):typeof n==`string`&&n.endsWith(`%`)?t(Number.parseFloat(n)/100*r,0,r):e.option.margin[1]}get fontSize(){let{clamp:t}=this.utils,{clientHeight:n}=this.$player,r=this.option.fontSize;if(typeof r==`number`)return Math.round(t(r,12,n));if(typeof r==`string`&&r.endsWith(`%`)){let e=Number.parseFloat(r)/100;return Math.round(t(n*e,12,n))}return e.option.fontSize}get $ref(){let t=this.$refs.pop()||document.createElement(`div`);return t.style.cssText=e.cssText,t.dataset.mode=``,t.dataset.id=``,t.className=``,t}get readys(){let{currentTime:e}=this.art,t=[];return this.filter(`ready`,e=>t.push(e)),this.filter(`wait`,n=>{e+.1>=n.time&&n.time>=e-.1&&t.push(n)}),t}get visibles(){let e=[],{clientWidth:t}=this.$player,n=this.getLeft(this.$player);return this.filter(`emit`,r=>{let i=r.$ref.offsetTop,a=this.getLeft(r.$ref)-n,o=r.$ref.clientHeight,s=r.$ref.clientWidth,c=a+s,l=t-c,u=c/r.$restTime,d={};d.top=i,d.left=a,d.height=o,d.width=s,d.right=l,d.speed=u,d.distance=c,d.time=r.$restTime,d.mode=r.mode,e.push(d)}),e}get speed(){return this.option.synchronousPlayback&&this.art.playbackRate?this.option.speed/Number(this.art.playbackRate):this.option.speed}async load(e){let{errorHandle:t}=this.utils,n=[],r=e||this.option.danmuku;try{typeof r==`function`?n=await r():r instanceof Promise?n=await r:typeof r==`string`?n=await L(r):Array.isArray(r)&&(n=r),t(Array.isArray(n),`Danmuku need return an array as result`),e===void 0&&(this.reset(),this.queue=[],this.states={wait:[],ready:[],emit:[],stop:[]},this.$refs=[],this.$danmuku.textContent=``);for(let e=0;e<n.length;e++){let t=n[e];await this.emit(t)}this.art.emit(`artplayerPluginDanmuku:loaded`,this.queue)}catch(e){throw this.art.emit(`artplayerPluginDanmuku:error`,e),e}return this}async emit(e){let{clamp:t}=this.utils;if(this.validator(e,{id:`?string`,text:`string`,mode:`?number`,color:`?string`,time:`?number`,border:`?boolean`,style:`?object`}),!e.text.trim()||(e.time=e.time?t(e.time,0,1/0):this.art.currentTime+.5,e.mode===void 0&&(e.mode=this.option.mode),e.style===void 0&&(e.style={}),e.color===void 0&&(e.color=this.option.color),![0,1,2].includes(e.mode))||!this.option.filter(e))return this;let n={...e,$state:`wait`,$index:this.index++,$ref:null,$restTime:0,$lastStartTime:0};return this.setState(n,`wait`),this.queue.push(n),this}config(t,n=!1){let{clamp:r}=this.utils,{$controlsCenter:i}=this.art.template;return!Object.keys(t).some(e=>JSON.stringify(this.option[e])!==JSON.stringify(t[e]))&&!n?this:(this.option=Object.assign({},e.option,this.option,t),this.validator(this.option,e.scheme),this.option.mode=r(this.option.mode,0,2),this.option.speed=r(this.option.speed,1,10),this.option.opacity=r(this.option.opacity,0,1),this.option.lockTime=r(this.option.lockTime,1,60),this.option.maxLength=r(this.option.maxLength,1,1e3),this.option.mount=this.option.mount||i,t.fontSize&&this.reset(),this.option.visible?this.show():this.hide(),this.art.emit(`artplayerPluginDanmuku:config`,this.option),this)}getLeft(e){let t=e.getBoundingClientRect();return this.isRotate?t.top:t.left}postMessage(e={}){return new Promise(t=>{e.id=Date.now(),this.worker.postMessage(e),this.worker.onmessage=n=>{let{data:r}=n;r.id===e.id&&t(r)}})}filter(e,t){let n=this.states[e]||[];for(let e=0;e<n.length;e++)t(n[e]);return n}setState(e,t){this.states[e.$state]=this.states[e.$state].filter(t=>t!==e),e.$state=t,e.$ref&&(e.$ref.dataset.state=t),this.states[t].push(e)}makeWait(t){this.setState(t,`wait`),t.$ref&&(t.$ref.style.cssText=e.cssText,t.$ref.style.visibility=`hidden`,t.$ref.style.marginLeft=`0px`,t.$ref.style.transform=`translateX(0px)`,t.$ref.style.transition=`transform 0s linear 0s`,this.$refs.push(t.$ref),t.$ref=null)}update(){let{setStyles:e}=this.utils;return this.timer=window.requestAnimationFrame(async()=>{if(this.art.playing&&!this.isHide){this.filter(`emit`,e=>{let t=(Date.now()-e.$lastStartTime)/1e3;e.$restTime-=t,e.$lastStartTime=Date.now(),e.$restTime<=0&&this.makeWait(e)});let t=this.readys;for(let n=0;n<t.length;n++){let r=t[n];if(await this.option.beforeVisible(r)){let{clientWidth:t,clientHeight:n}=this.$player;r.$ref=this.$ref,r.$ref.textContent=r.text,this.$danmuku.appendChild(r.$ref),r.$ref.style.opacity=this.option.opacity,r.$ref.style.fontSize=`${this.fontSize}px`,r.$ref.style.color=r.color,r.$ref.style.border=r.border?`1px solid ${r.color}`:null,r.$ref.style.backgroundColor=r.border?`rgb(0 0 0 / 50%)`:null,e(r.$ref,r.style),r.$lastStartTime=Date.now(),r.$restTime=this.speed;let i=t+r.$ref.clientWidth,{result:a}=await this.postMessage({type:`getDanmuTop`,target:{mode:r.mode,height:r.$ref.clientHeight,speed:i/r.$restTime},visibles:this.visibles,antiOverlap:this.option.antiOverlap,clientWidth:t,clientHeight:n,marginBottom:this.marginBottom,marginTop:this.marginTop});if(r.$ref){if(!this.isStop&&a!==void 0){switch(this.setState(r,`emit`),r.$ref.style.top=`${a}px`,r.$ref.style.visibility=`visible`,r.$ref.dataset.mode=r.mode,r.$ref.dataset.id=r.id||``,r.mode){case 0:r.$ref.style.left=`${t}px`,r.$ref.style.marginLeft=`0px`,r.$ref.style.transform=`translateX(${-i}px)`,r.$ref.style.transition=`transform ${r.$restTime}s linear 0s`;break;case 1:case 2:r.$ref.style.left=`50%`,r.$ref.style.marginLeft=`-${r.$ref.clientWidth/2}px`}this.art.emit(`artplayerPluginDanmuku:visible`,r)}else this.setState(r,`ready`),this.$refs.push(r.$ref),r.$ref=null}}}}this.isStop||this.update()}),this}resize(){let{clientWidth:e}=this.$player;this.filter(`stop`,t=>{t.mode===0&&(t.$ref.style.left=`${e}px`)}),this.filter(`emit`,t=>{switch(t.$lastStartTime=Date.now(),t.mode){case 0:{let n=e+t.$ref.clientWidth;t.$ref.style.left=`${e}px`,t.$ref.style.transform=`translateX(${-n}px)`,t.$ref.style.transition=`transform ${t.$restTime}s linear 0s`;break}}})}continue(){let{clientWidth:e}=this.$player;return this.filter(`stop`,t=>{switch(this.setState(t,`emit`),t.$lastStartTime=Date.now(),t.mode){case 0:{let n=e+t.$ref.clientWidth;t.$ref.style.transform=`translateX(${-n}px)`,t.$ref.style.transition=`transform ${t.$restTime}s linear 0s`;break}}}),this}suspend(){let{clientWidth:e}=this.$player;return this.filter(`emit`,t=>{switch(this.setState(t,`stop`),t.mode){case 0:{let n=e-(this.getLeft(t.$ref)-this.getLeft(this.$player));t.$ref.style.transform=`translateX(${-n}px)`,t.$ref.style.transition=`transform 0s linear 0s`;break}}}),this}stop(){return this.isStop=!0,this.suspend(),window.cancelAnimationFrame(this.timer),this.art.emit(`artplayerPluginDanmuku:stop`),this}start(){return this.isStop=!1,this.continue(),this.update(),this.art.emit(`artplayerPluginDanmuku:start`),this}reset(){return this.queue.forEach(e=>this.makeWait(e)),this.art.emit(`artplayerPluginDanmuku:reset`),this}show(){return this.isHide=!1,this.$danmuku.style.opacity=1,this.option.visible=!0,this.art.emit(`artplayerPluginDanmuku:show`),this}hide(){return this.isHide=!0,this.$danmuku.style.opacity=0,this.option.visible=!1,this.art.emit(`artplayerPluginDanmuku:hide`),this}destroy(){this.stop(),this.worker.terminate(),this.art.off(`video:play`,this.start),this.art.off(`video:playing`,this.start),this.art.off(`video:pause`,this.stop),this.art.off(`video:waiting`,this.stop),this.art.off(`resize`,this.reset),this.art.off(`destroy`,this.destroy),this.art.emit(`artplayerPluginDanmuku:destroy`)}},l={map(e,t,n,r,i){return(e-t)*(i-r)/(n-t)+r},range(e,t,n){let r=Math.round(e/n)*n;return Array.from({length:Math.floor((t-e)/n)},(e,t)=>t*n+r)}},u=`<svg  class="apd-icon apd-check-off" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" data-pointer="none" viewBox="0 0 32 32" width="32"  height="32" ><path d="M8 6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2H8zm0-2h16c2.21 0 4 1.79 4 4v16c0 2.21-1.79 4-4 4H8c-2.21 0-4-1.79-4-4V8c0-2.21 1.79-4 4-4z" fill="#FFFFFF"></path></svg>`,d=`<svg class="apd-icon apd-check-on" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" data-pointer="none" viewBox="0 0 32 32" width="32"  height="32" ><path d="m13 18.25-1.8-1.8c-.6-.6-1.65-.6-2.25 0s-.6 1.5 0 2.25l2.85 2.85c.318.318.762.468 1.2.448.438.02.882-.13 1.2-.448l8.85-8.85c.6-.6.6-1.65 0-2.25s-1.65-.6-2.25 0l-7.8 7.8zM8 4h16c2.21 0 4 1.79 4 4v16c0 2.21-1.79 4-4 4H8c-2.21 0-4-1.79-4-4V8c0-2.21 1.79-4 4-4z" fill="#00AEEC"></path></svg>`,f=`<svg class="apd-icon apd-config-icon" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" data-pointer="none" viewBox="0 0 24 24" width="24"  height="24" ><path fill-rule="evenodd" d="m15.645 4.881 1.06-1.473a.998.998 0 1 0-1.622-1.166L13.22 4.835a110.67 110.67 0 0 0-1.1-.007h-.131c-.47 0-.975.004-1.515.012L8.783 2.3A.998.998 0 0 0 7.12 3.408l.988 1.484c-.688.019-1.418.042-2.188.069a4.013 4.013 0 0 0-3.83 3.44c-.165 1.15-.245 2.545-.245 4.185 0 1.965.115 3.67.35 5.116a4.012 4.012 0 0 0 3.763 3.363c1.903.094 3.317.141 5.513.141a.988.988 0 0 0 0-1.975 97.58 97.58 0 0 1-5.416-.139 2.037 2.037 0 0 1-1.91-1.708c-.216-1.324-.325-2.924-.325-4.798 0-1.563.076-2.864.225-3.904.14-.977.96-1.713 1.945-1.747 2.444-.087 4.465-.13 6.063-.131 1.598 0 3.62.044 6.064.13.96.034 1.71.81 1.855 1.814.075.524.113 1.962.141 3.065v.002c.005.183.01.07.014-.038.004-.096.008-.189.011-.081a.987.987 0 1 0 1.974-.069c-.004-.105-.007-.009-.011.09-.002.056-.004.112-.007.135l-.002.01a.574.574 0 0 1-.005-.091v-.027c-.03-1.118-.073-2.663-.16-3.276-.273-1.906-1.783-3.438-3.74-3.507-.905-.032-1.752-.058-2.543-.079Zm-3.113 4.703h-1.307v4.643h2.2v.04l.651-1.234c.113-.215.281-.389.482-.509v-.11h.235c.137-.049.283-.074.433-.074h1.553V9.584h-1.264a8.5 8.5 0 0 0 .741-1.405l-1.078-.381c-.24.631-.501 1.23-.806 1.786h-1.503l.686-.305c-.228-.501-.5-.959-.806-1.394l-1.034.348c.294.392.566.839.817 1.35Zm-1.7 5.502h2.16l-.564 1.068h-1.595v-1.068Zm-2.498-1.863.152-1.561h1.96V8.289H7.277v.969h2.048v1.435h-1.84l-.306 3.51h2.254c0 1.155-.043 1.906-.12 2.255-.076.348-.38.523-.925.523-.305 0-.61-.022-.893-.055l.294 1.056.061.005c.282.02.546.039.81.039.991-.065 1.547-.414 1.677-1.046.11-.631.175-1.883.175-3.757H8.334Zm5.09-.8v.85h-1.188v-.85h1.187Zm-1.188-.955h1.187v-.893h-1.187v.893Zm2.322.007v-.893h1.241v.893h-1.241Zm.528 2.757a1.26 1.26 0 0 1 1.087-.627l4.003-.009a1.26 1.26 0 0 1 1.094.63l1.721 2.982c.226.39.225.872-.001 1.263l-1.743 3a1.26 1.26 0 0 1-1.086.628l-4.003.009a1.26 1.26 0 0 1-1.094-.63l-1.722-2.982a1.26 1.26 0 0 1 .002-1.263l1.742-3Zm1.967.858a1.26 1.26 0 0 0-1.08.614l-.903 1.513a1.26 1.26 0 0 0-.002 1.289l.885 1.492c.227.384.64.62 1.086.618l2.192-.005a1.26 1.26 0 0 0 1.08-.615l.904-1.518a1.26 1.26 0 0 0 .001-1.288l-.884-1.489a1.26 1.26 0 0 0-1.086-.616l-2.193.005Zm2.517 2.76a1.4 1.4 0 1 1-2.8 0 1.4 1.4 0 0 1 2.8 0Z" clip-rule="evenodd"></path></svg>`,p=`<svg class="apd-icon apd-mode-0-off" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" data-pointer="none" style="enable-background:new 0 0 28 28" viewBox="0 0 28 28" width="28"  height="28" ><path d="M23 15c1.487 0 2.866.464 4 1.255V7a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v14a4 4 0 0 0 4 4h11.674A7 7 0 0 1 23 15zM11 9h6a1 1 0 0 1 0 2h-6a1 1 0 0 1 0-2zm-3 2H6V9h2v2zm4 4h-2v-2h2v2zm2-1a1 1 0 0 1 1-1h1a1 1 0 0 1 0 2h-1a1 1 0 0 1-1-1z" fill="#00AEEC"></path><path d="M26.536 18.464a5 5 0 0 0-7.071 0 5 5 0 0 0 0 7.071 5 5 0 1 0 7.071-7.071zm-5.657 5.657a3 3 0 0 1-.586-3.415l4.001 4.001a3 3 0 0 1-3.415-.586zm4.829-.827-4.001-4.001a3.002 3.002 0 0 1 4.001 4.001z" fill="#00AEEC"></path></svg>`,m=`<svg class="apd-icon apd-mode-0-on" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" data-pointer="none" style="enable-background:new 0 0 28 28" viewBox="0 0 28 28" width="28"  height="28" ><path d="M23 3H5a4 4 0 0 0-4 4v14a4 4 0 0 0 4 4h18a4 4 0 0 0 4-4V7a4 4 0 0 0-4-4zM11 9h6a1 1 0 0 1 0 2h-6a1 1 0 0 1 0-2zm-3 2H6V9h2v2zm4 4h-2v-2h2v2zm9 0h-6a1 1 0 0 1 0-2h6a1 1 0 0 1 0 2z" fill="#FFFFFF"></path></svg>`,h=`<svg class="apd-icon apd-mode-1-off" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" data-pointer="none" style="enable-background:new 0 0 28 28" viewBox="0 0 28 28" width="28"  height="28" ><path d="M23 15c1.487 0 2.866.464 4 1.255V7a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v14a4 4 0 0 0 4 4h11.674A7 7 0 0 1 23 15zm-4-8h2v2h-2V7zM9 9H7V7h2v2zm4 0h-2V7h2v2zm2-2h2v2h-2V7z" fill="#00AEEC"></path><path d="M26.536 18.464a5 5 0 0 0-7.071 0 5 5 0 0 0 0 7.071 5 5 0 1 0 7.071-7.071zm-5.657 5.657a3 3 0 0 1-.586-3.415l4.001 4.001a3 3 0 0 1-3.415-.586zm4.829-.827-4.001-4.001a3.002 3.002 0 0 1 4.001 4.001z" fill="#00AEEC"></path></svg>`,g=`<svg class="apd-icon apd-mode-1-on" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" data-pointer="none" style="enable-background:new 0 0 28 28" viewBox="0 0 28 28" width="28"  height="28" ><path d="M23 3H5a4 4 0 0 0-4 4v14a4 4 0 0 0 4 4h18a4 4 0 0 0 4-4V7a4 4 0 0 0-4-4zM9 9H7V7h2v2zm4 0h-2V7h2v2zm4 0h-2V7h2v2zm4 0h-2V7h2v2z" fill="#FFFFFF"></path></svg>`,_=`<svg class="apd-icon apd-mode-2-off" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" data-pointer="none" style="enable-background:new 0 0 28 28" viewBox="0 0 28 28" width="28"  height="28" ><path d="M23 15c1.487 0 2.866.464 4 1.255V7a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v14a4 4 0 0 0 4 4h11.674A7 7 0 0 1 23 15zM9 21H7v-2h2v2zm4 0h-2v-2h2v2z" fill="#00AEEC"></path><path d="M26.536 18.464a5 5 0 0 0-7.071 0 5 5 0 0 0 0 7.071 5 5 0 1 0 7.071-7.071zm-5.657 5.657a3 3 0 0 1-.586-3.415l4.001 4.001a3 3 0 0 1-3.415-.586zm4.829-.827-4.001-4.001a3.002 3.002 0 0 1 4.001 4.001z" fill="#00AEEC"></path></svg>`,v=`<svg class="apd-icon apd-mode-2-on" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" data-pointer="none" style="enable-background:new 0 0 28 28" viewBox="0 0 28 28" width="28"  height="28" ><path d="M23 3H5a4 4 0 0 0-4 4v14a4 4 0 0 0 4 4h18a4 4 0 0 0 4-4V7a4 4 0 0 0-4-4zM9 21H7v-2h2v2zm4 0h-2v-2h2v2zm4 0h-2v-2h2v2zm4 0h-2v-2h2v2z" fill="#FFFFFF"></path></svg>`,y=`<svg class="apd-icon apd-toggle-off" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" data-pointer="none" viewBox="0 0 24 24" width="24"  height="24" ><path fill-rule="evenodd" d="m8.085 4.891-.999-1.499a1.008 1.008 0 0 1 1.679-1.118l1.709 2.566c.54-.008 1.045-.012 1.515-.012h.13c.345 0 .707.003 1.088.007l1.862-2.59a1.008 1.008 0 0 1 1.637 1.177l-1.049 1.46c.788.02 1.631.046 2.53.078 1.958.069 3.468 1.6 3.74 3.507.088.613.13 2.158.16 3.276l.001.027c.01.333.017.63.025.856a.987.987 0 0 1-1.974.069c-.008-.23-.016-.539-.025-.881v-.002c-.028-1.103-.066-2.541-.142-3.065-.143-1.004-.895-1.78-1.854-1.813-2.444-.087-4.466-.13-6.064-.131-1.598 0-3.619.044-6.063.13a2.037 2.037 0 0 0-1.945 1.748c-.15 1.04-.225 2.341-.225 3.904 0 1.874.11 3.474.325 4.798.154.949.95 1.66 1.91 1.708a97.58 97.58 0 0 0 5.416.139.988.988 0 0 1 0 1.975c-2.196 0-3.61-.047-5.513-.141A4.012 4.012 0 0 1 2.197 17.7c-.236-1.446-.351-3.151-.351-5.116 0-1.64.08-3.035.245-4.184A4.013 4.013 0 0 1 5.92 4.96c.761-.027 1.483-.05 2.164-.069Zm4.436 4.707h-1.32v4.63h2.222v.848h-2.618v1.078h2.431a5.01 5.01 0 0 1 3.575-3.115V9.598h-1.276a8.59 8.59 0 0 0 .748-1.42l-1.089-.384a14.232 14.232 0 0 1-.814 1.804h-1.518l.693-.308a8.862 8.862 0 0 0-.814-1.408l-1.045.352c.297.396.572.847.825 1.364Zm-4.18 3.564.154-1.485h1.98V8.289h-3.2v.979h2.067v1.43H7.483l-.308 3.454h2.277c0 1.166-.044 1.925-.12 2.277-.078.352-.386.528-.936.528-.308 0-.616-.022-.902-.055l.297 1.067.062.004c.285.02.551.04.818.04 1.001-.066 1.562-.418 1.694-1.056.11-.638.176-1.903.176-3.795h-2.2Zm7.458.11v-.858h-1.254v.858H15.8Zm-2.376-.858v.858h-1.199v-.858h1.2Zm-1.199-.946h1.2v-.902h-1.2v.902Zm2.321 0v-.902H15.8v.902h-1.254Zm3.517 10.594a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm-.002-1.502a2.5 2.5 0 0 1-2.217-3.657l3.326 3.398a2.49 2.49 0 0 1-1.109.259Zm2.5-2.5c0 .42-.103.815-.286 1.162l-3.328-3.401a2.5 2.5 0 0 1 3.614 2.239Z" clip-rule="evenodd"></path></svg>`,b=`<svg class="apd-icon apd-toggle-on" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" data-pointer="none" viewBox="0 0 24 24" width="24"  height="24" ><path fill-rule="evenodd" d="M11.989 4.828c-.47 0-.975.004-1.515.012l-1.71-2.566a1.008 1.008 0 0 0-1.678 1.118l.999 1.5c-.681.018-1.403.04-2.164.068a4.013 4.013 0 0 0-3.83 3.44c-.165 1.15-.245 2.545-.245 4.185 0 1.965.115 3.67.35 5.116a4.012 4.012 0 0 0 3.763 3.363l.906.046c1.205.063 1.808.095 3.607.095a.988.988 0 0 0 0-1.975c-1.758 0-2.339-.03-3.501-.092l-.915-.047a2.037 2.037 0 0 1-1.91-1.708c-.216-1.324-.325-2.924-.325-4.798 0-1.563.076-2.864.225-3.904.14-.977.96-1.713 1.945-1.747 2.444-.087 4.465-.13 6.063-.131 1.598 0 3.62.044 6.064.13.96.034 1.71.81 1.855 1.814.075.524.113 1.962.141 3.065v.002c.01.342.017.65.025.88a.987.987 0 1 0 1.974-.068c-.008-.226-.016-.523-.025-.856v-.027c-.03-1.118-.073-2.663-.16-3.276-.273-1.906-1.783-3.438-3.74-3.507-.9-.032-1.743-.058-2.531-.078l1.05-1.46a1.008 1.008 0 0 0-1.638-1.177l-1.862 2.59c-.38-.004-.744-.007-1.088-.007h-.13Zm.521 4.775h-1.32v4.631h2.222v.847h-2.618v1.078h2.618l.003.678c.36.026.714.163 1.01.407h.11v-1.085h2.694v-1.078h-2.695v-.847H16.8v-4.63h-1.276a8.59 8.59 0 0 0 .748-1.42L15.183 7.8a14.232 14.232 0 0 1-.814 1.804h-1.518l.693-.308a8.862 8.862 0 0 0-.814-1.408l-1.045.352c.297.396.572.847.825 1.364Zm-4.18 3.564.154-1.485h1.98V8.294h-3.2v.98H9.33v1.43H7.472l-.308 3.453h2.277c0 1.166-.044 1.925-.12 2.277-.078.352-.386.528-.936.528-.308 0-.616-.022-.902-.055l.297 1.067.062.005c.285.02.551.04.818.04 1.001-.067 1.562-.419 1.694-1.057.11-.638.176-1.903.176-3.795h-2.2Zm7.458.11v-.858h-1.254v.858h1.254Zm-2.376-.858v.858h-1.199v-.858h1.2Zm-1.199-.946h1.2v-.902h-1.2v.902Zm2.321 0v-.902h1.254v.902h-1.254Z" clip-rule="evenodd"></path><path fill="#00AEEC" fill-rule="evenodd" d="M22.846 14.627a1 1 0 0 0-1.412.075l-5.091 5.703-2.216-2.275-.097-.086-.008-.005a1 1 0 0 0-1.322 1.493l2.963 3.041.093.083.007.005c.407.315 1 .27 1.354-.124l5.81-6.505.08-.102.005-.008a1 1 0 0 0-.166-1.295Z" clip-rule="evenodd"></path></svg>`,x=`<svg class="apd-icon apd-style-icon" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" data-pointer="none" style="enable-background:new 0 0 22 22" viewBox="0 0 22 22" width="36"  height="24" ><path d="M17 16H5c-.55 0-1 .45-1 1s.45 1 1 1h12c.55 0 1-.45 1-1s-.45-1-1-1zM6.96 15c.39 0 .74-.24.89-.6l.65-1.6h5l.66 1.6c.15.36.5.6.89.6.69 0 1.15-.71.88-1.34l-3.88-8.97C11.87 4.27 11.46 4 11 4s-.87.27-1.05.69l-3.88 8.97c-.27.63.2 1.34.89 1.34zM11 5.98 12.87 11H9.13L11 5.98z"></path></svg>`,S=`.artplayer-plugin-danmuku {
  display: flex;
  position: relative;
  z-index: 99;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  height: 32px;
  width: 100%;
  color: #fff;
  font-weight: 300;
  flex-shrink: 0;
  gap: 10px;
}
.artplayer-plugin-danmuku .apd-icon {
  cursor: pointer;
  opacity: 0.75;
  transition: all 0.2s ease;
  fill: #fff;
}
.artplayer-plugin-danmuku .apd-icon:hover {
  opacity: 1;
}
.artplayer-plugin-danmuku .apd-config {
  display: flex;
  position: relative;
}
.artplayer-plugin-danmuku .apd-config .apd-config-panel {
  position: absolute;
  bottom: 24px;
  left: 0;
  width: 320px;
  padding: 10px;
  opacity: 0;
  pointer-events: none;
}
.artplayer-plugin-danmuku .apd-config .apd-config-panel .apd-config-panel-inner {
  width: 100%;
  border-radius: 3px;
  background-color: rgba(0, 0, 0, 0.85);
  padding: 10px;
}
.artplayer-plugin-danmuku .apd-config:hover .apd-config-panel {
  opacity: 100;
  pointer-events: all;
}
.artplayer-plugin-danmuku .apd-config-mode,
.artplayer-plugin-danmuku .apd-config-slider,
.artplayer-plugin-danmuku .apd-config-other,
.artplayer-plugin-danmuku .apd-style-mode {
  margin-bottom: 15px;
}
.artplayer-plugin-danmuku .apd-modes {
  display: flex;
  align-items: center;
  margin-top: 5px;
  gap: 20px;
}
.artplayer-plugin-danmuku .apd-modes .apd-mode {
  cursor: pointer;
  text-align: center;
}
.artplayer-plugin-danmuku .apd-modes .apd-mode:hover {
  color: #00a1d6;
}
.artplayer-plugin-danmuku .apd-config-slider {
  display: flex;
  align-items: center;
  gap: 12px;
}
.artplayer-plugin-danmuku .apd-config-slider .apd-value {
  width: 32px;
  text-align: right;
}
.artplayer-plugin-danmuku .apd-slider {
  position: relative;
  flex: 1;
  display: flex;
  height: 20px;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.artplayer-plugin-danmuku .apd-slider .apd-slider-line {
  position: relative;
  height: 2px;
  width: 100%;
  overflow: hidden;
  border-radius: 3px;
  background-color: rgba(255, 255, 255, 0.25);
}
.artplayer-plugin-danmuku .apd-slider .apd-slider-points {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.artplayer-plugin-danmuku .apd-slider .apd-slider-points .apd-slider-point {
  width: 2px;
  height: 2px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
}
.artplayer-plugin-danmuku .apd-slider .apd-slider-progress {
  width: 0%;
  height: 100%;
  background-color: #00a1d6;
}
.artplayer-plugin-danmuku .apd-slider .apd-slider-dot {
  position: absolute;
  transform: translateX(-6px);
  left: 0%;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #00a1d6;
}
.artplayer-plugin-danmuku .apd-slider .apd-slider-steps {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: absolute;
  bottom: -12px;
  width: calc(100% + 32px);
  color: #777;
}
.artplayer-plugin-danmuku .apd-slider .apd-slider-steps .apd-slider-step {
  flex-shrink: 0;
  width: 36px;
  text-align: center;
  scale: 0.95;
}
.artplayer-plugin-danmuku .apd-config-other {
  display: flex;
  align-items: center;
  gap: 20px;
}
.artplayer-plugin-danmuku .apd-config-other .apd-check-off,
.artplayer-plugin-danmuku .apd-config-other .apd-check-on {
  width: 16px;
  height: 16px;
}
.artplayer-plugin-danmuku .apd-config-other .apd-other {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 2px;
}
.artplayer-plugin-danmuku .apd-config-other .apd-other:hover {
  color: #00a1d6;
}
.artplayer-plugin-danmuku .apd-emitter {
  display: flex;
  flex: 1;
  align-items: center;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.25);
  border-radius: 5px;
}
.artplayer-plugin-danmuku .apd-style {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.artplayer-plugin-danmuku .apd-style .apd-style-panel {
  position: absolute;
  bottom: 24px;
  left: 0;
  width: 200px;
  padding: 10px;
  opacity: 0;
  pointer-events: none;
}
.artplayer-plugin-danmuku .apd-style .apd-style-panel .apd-style-panel-inner {
  width: 100%;
  border-radius: 3px;
  background-color: rgba(0, 0, 0, 0.85);
  padding: 10px;
}
.artplayer-plugin-danmuku .apd-style:hover .apd-style-panel {
  opacity: 100;
  pointer-events: all;
}
.artplayer-plugin-danmuku .apd-colors {
  display: flex;
  flex-wrap: wrap;
  margin-top: 5px;
  gap: 8px;
}
.artplayer-plugin-danmuku .apd-colors .apd-color {
  width: 16px;
  height: 16px;
  border-radius: 2px;
  cursor: pointer;
}
.artplayer-plugin-danmuku .apd-colors .apd-color.apd-active {
  border: 1px solid black;
  box-shadow: 0 0 0 1px #fff;
}
.artplayer-plugin-danmuku .apd-input {
  outline: none;
  height: 100%;
  flex: 1;
  min-width: 0;
  width: auto;
  border: none;
  line-height: 1;
  color: #fff;
  background-color: transparent;
}
.artplayer-plugin-danmuku .apd-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}
.artplayer-plugin-danmuku .apd-send {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 60px;
  flex-shrink: 0;
  cursor: pointer;
  text-shadow: none;
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
  background-color: #00a1d6;
}
.artplayer-plugin-danmuku .apd-send.apd-lock {
  cursor: not-allowed;
  color: #666;
  background-color: #e7e7e7;
}
.art-controls-center .apd-emitter {
  width: 260px;
  flex: none;
}
.art-fullscreen .artplayer-plugin-danmuku,
.art-fullscreen-web .artplayer-plugin-danmuku {
  height: 38px;
  gap: 16px;
}
.art-fullscreen .artplayer-plugin-danmuku .apd-config-icon,
.art-fullscreen-web .artplayer-plugin-danmuku .apd-config-icon,
.art-fullscreen .artplayer-plugin-danmuku .apd-toggle-off,
.art-fullscreen-web .artplayer-plugin-danmuku .apd-toggle-off,
.art-fullscreen .artplayer-plugin-danmuku .apd-toggle-on,
.art-fullscreen-web .artplayer-plugin-danmuku .apd-toggle-on {
  width: 28px;
  height: 28px;
}
.art-fullscreen .artplayer-plugin-danmuku .apd-emitter,
.art-fullscreen-web .artplayer-plugin-danmuku .apd-emitter {
  width: 400px;
  flex: none;
}
.art-video-player > .artplayer-plugin-danmuku {
  position: absolute;
  left: 0;
  right: 0;
  bottom: -40px;
  padding: 0 10px;
}
.art-video-player:has(> .artplayer-plugin-danmuku) {
  margin-bottom: 40px;
}
[data-danmuku-emitter='false'] .apd-emitter {
  display: none !important;
}
[data-danmuku-emitter='false'] .art-controls-center .artplayer-plugin-danmuku {
  justify-content: flex-end;
  gap: 18px;
}
[data-danmuku-emitter='false'].art-fullscreen .art-controls-center .artplayer-plugin-danmuku,
[data-danmuku-emitter='false'].art-fullscreen-web .art-controls-center .artplayer-plugin-danmuku {
  gap: 24px;
}
[data-danmuku-theme='light'] > .artplayer-plugin-danmuku .apd-icon {
  fill: #333;
}
[data-danmuku-theme='light'] > .artplayer-plugin-danmuku .apd-emitter {
  background-color: #f1f2f3;
}
[data-danmuku-theme='light'] > .artplayer-plugin-danmuku .apd-input {
  color: #000;
}
[data-danmuku-theme='light'] > .artplayer-plugin-danmuku .apd-input::placeholder {
  color: rgba(0, 0, 0, 0.3);
}
[data-danmuku-visible='false'] .apd-toggle-off {
  display: block;
}
[data-danmuku-visible='false'] .apd-toggle-on {
  display: none;
}
[data-danmuku-visible='true'] .apd-toggle-off {
  display: none;
}
[data-danmuku-visible='true'] .apd-toggle-on {
  display: block;
}
[data-danmuku-anti-overlap='false'] .apd-anti-overlap .apd-check-on {
  display: none;
}
[data-danmuku-anti-overlap='false'] .apd-anti-overlap .apd-check-off {
  display: block;
}
[data-danmuku-anti-overlap='true'] .apd-anti-overlap .apd-check-on {
  display: block;
}
[data-danmuku-anti-overlap='true'] .apd-anti-overlap .apd-check-off {
  display: none;
}
[data-danmuku-sync-video='false'] .apd-sync-video .apd-check-on {
  display: none;
}
[data-danmuku-sync-video='false'] .apd-sync-video .apd-check-off {
  display: block;
}
[data-danmuku-sync-video='true'] .apd-sync-video .apd-check-on {
  display: block;
}
[data-danmuku-sync-video='true'] .apd-sync-video .apd-check-off {
  display: none;
}
[data-danmuku-mode0='false'] .apd-config-mode .apd-mode-0-off {
  display: block;
}
[data-danmuku-mode0='false'] .apd-config-mode .apd-mode-0-on {
  display: none;
}
[data-danmuku-mode0='false'] .art-danmuku [data-mode='0'] {
  opacity: 0 !important;
}
[data-danmuku-mode0='true'] .apd-config-mode .apd-mode-0-off {
  display: none;
}
[data-danmuku-mode0='true'] .apd-config-mode .apd-mode-0-on {
  display: block;
}
[data-danmuku-mode='0'] .apd-style-mode [data-mode='0'] {
  color: #00a1d6;
}
[data-danmuku-mode='0'] .apd-style-mode [data-mode='0'] path {
  fill: #00a1d6;
}
[data-danmuku-mode1='false'] .apd-config-mode .apd-mode-1-off {
  display: block;
}
[data-danmuku-mode1='false'] .apd-config-mode .apd-mode-1-on {
  display: none;
}
[data-danmuku-mode1='false'] .art-danmuku [data-mode='1'] {
  opacity: 0 !important;
}
[data-danmuku-mode1='true'] .apd-config-mode .apd-mode-1-off {
  display: none;
}
[data-danmuku-mode1='true'] .apd-config-mode .apd-mode-1-on {
  display: block;
}
[data-danmuku-mode='1'] .apd-style-mode [data-mode='1'] {
  color: #00a1d6;
}
[data-danmuku-mode='1'] .apd-style-mode [data-mode='1'] path {
  fill: #00a1d6;
}
[data-danmuku-mode2='false'] .apd-config-mode .apd-mode-2-off {
  display: block;
}
[data-danmuku-mode2='false'] .apd-config-mode .apd-mode-2-on {
  display: none;
}
[data-danmuku-mode2='false'] .art-danmuku [data-mode='2'] {
  opacity: 0 !important;
}
[data-danmuku-mode2='true'] .apd-config-mode .apd-mode-2-off {
  display: none;
}
[data-danmuku-mode2='true'] .apd-config-mode .apd-mode-2-on {
  display: block;
}
[data-danmuku-mode='2'] .apd-style-mode [data-mode='2'] {
  color: #00a1d6;
}
[data-danmuku-mode='2'] .apd-style-mode [data-mode='2'] path {
  fill: #00a1d6;
}
`,C=class{constructor(e,t){this.art=e,this.danmuku=t,this.utils=e.constructor.utils;let{setStyle:n}=this.utils,{$controlsCenter:r}=e.template;n(r,`display`,`flex`),this.template={$controlsCenter:r,$mount:r,$danmuku:null,$toggle:null,$config:null,$configPanel:null,$configModes:null,$style:null,$stylePanel:null,$styleModes:null,$colors:null,$opacitySlider:null,$opacityValue:null,$marginSlider:null,$marginValue:null,$fontSizeSlider:null,$fontSizeValue:null,$speedSlider:null,$speedValue:null,$input:null,$send:null},this.slider={opacity:null,margin:null,fontSize:null,speed:null},this.emitting=!1,this.isLock=!1,this.timer=null,this.createTemplate(),this.createSliders(),this.createEvents(),this.mount(this.option.mount),e.on(`resize`,()=>this.resize()),e.on(`fullscreen`,e=>this.onFullscreen(e)),e.on(`fullscreenWeb`,e=>this.onFullscreen(e)),e.proxy(this.template.$config,`mouseenter`,()=>{this.onMouseEnter({$control:this.template.$config,$panel:this.template.$configPanel})}),e.proxy(this.template.$style,`mouseenter`,()=>{this.onMouseEnter({$control:this.template.$style,$panel:this.template.$stylePanel})})}static get icons(){return{$on:b,$off:y,$config:f,$style:x,$mode_0_off:p,$mode_0_on:m,$mode_1_off:h,$mode_1_on:g,$mode_2_off:_,$mode_2_on:v,$check_on:d,$check_off:u}}get option(){return this.danmuku.option}get outside(){return this.template.$mount!==this.template.$controlsCenter}get TEMPLATE(){let{option:e}=this;return`
            <div class="apd-toggle">
                ${b}${y}
            </div>
            <div class="apd-config">
                ${f}
                <div class="apd-config-panel">
                    <div class="apd-config-panel-inner">
                        <div class="apd-config-mode">
                            按类型屏蔽
                            <div class="apd-modes">
                                <div data-mode="0" class="apd-mode">
                                    ${p}${m}
                                    <div>滚动</div>
                                </div>
                                <div data-mode="1" class="apd-mode">
                                    ${h}${g}
                                    <div>顶部</div>
                                </div>
                                <div data-mode="2" class="apd-mode">
                                    ${_}${v}
                                    <div>底部</div>
                                </div>
                            </div>
                        </div>
                        <div class="apd-config-other">
                            <div class="apd-other apd-anti-overlap">
                                ${d}${u}
                                防止弹幕重叠
                            </div>
                            <div class="apd-other apd-sync-video">
                                ${d}${u}
                                同步视频速度
                            </div>
                        </div>
                        <div class="apd-config-slider apd-config-opacity">
                            不透明度
                            <div class="apd-slider"></div>
                            <div class="apd-value">未知</div>
                        </div>
                        <div class="apd-config-slider apd-config-margin">
                            显示区域
                            <div class="apd-slider"></div>
                            <div class="apd-value">未知</div>
                        </div>
                        <div class="apd-config-slider apd-config-fontSize">
                            弹幕字号
                            <div class="apd-slider"></div>
                            <div class="apd-value">未知</div>
                        </div>
                        <div class="apd-config-slider apd-config-speed">
                            弹幕速度
                            <div class="apd-slider"></div>
                            <div class="apd-value">未知</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="apd-emitter">
                <div class="apd-style">
                    ${x}
                    <div class="apd-style-panel">
                        <div class="apd-style-panel-inner">
                            <div class="apd-style-mode">
                                模式
                                <div class="apd-modes">
                                    <div data-mode="0" class="apd-mode">
                                        ${m}
                                        <div>滚动</div>
                                    </div>
                                    <div data-mode="1" class="apd-mode">
                                        ${g}
                                        <div>顶部</div>
                                    </div>
                                    <div data-mode="2" class="apd-mode">
                                        ${v}
                                        <div>底部</div>
                                    </div>
                                </div>
                            </div>
                            <div class="apd-style-color">
                                颜色
                                <div class="apd-colors">
                                    ${this.COLOR.map(e=>`<div data-color="${e}" class="apd-color" style="background-color: ${e}"></div>`).join(``)}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <input class="apd-input" placeholder="发个友善的弹幕见证当下" autocomplete="off" maxLength="${e.maxLength}" />
                <div class="apd-send">发送</div>
            </div>
        `}get OPACITY(){return{min:0,max:100,steps:[],...this.option.OPACITY}}get FONT_SIZE(){return{min:12,max:120,steps:[],...this.option.FONT_SIZE}}get MARGIN(){return{min:0,max:3,steps:[{name:`1/4`,value:[10,`75%`]},{name:`半屏`,value:[10,`50%`]},{name:`3/4`,value:[10,`25%`]},{name:`满屏`,value:[10,10]}],...this.option.MARGIN}}get SPEED(){return{min:0,max:4,steps:[{name:`极慢`,value:10},{name:`较慢`,value:7.5,hide:!0},{name:`适中`,value:5},{name:`较快`,value:2.5,hide:!0},{name:`极快`,value:1}],...this.option.SPEED}}get COLOR(){return this.option.COLOR.length?this.option.COLOR:[`#FE0302`,`#FF7204`,`#FFAA02`,`#FFD302`,`#FFFF00`,`#A0EE00`,`#00CD00`,`#019899`,`#4266BE`,`#89D5FF`,`#CC0273`,`#222222`,`#9B9B9B`,`#FFFFFF`]}query(e){let{query:t}=this.utils,{$danmuku:n}=this.template;return t(e,n)}append(e,t){let{append:n}=this.utils;[...e.children].includes(t)||n(e,t)}setData(e,t){let{$player:n}=this.art.template,{$mount:r}=this.template;n.dataset[e]=t,this.outside&&(r.dataset[e]=t)}createTemplate(){let{createElement:e,tooltip:t}=this.utils,n=e(`div`);n.className=`artplayer-plugin-danmuku`,n.innerHTML=this.TEMPLATE,this.template.$danmuku=n,this.template.$toggle=this.query(`.apd-toggle`),this.template.$config=this.query(`.apd-config`),this.template.$configPanel=this.query(`.apd-config-panel`),this.template.$configModes=this.query(`.apd-config-mode .apd-modes`),this.template.$style=this.query(`.apd-style`),this.template.$stylePanel=this.query(`.apd-style-panel`),this.template.$styleModes=this.query(`.apd-style-mode .apd-modes`),this.template.$colors=this.query(`.apd-colors`),this.template.$antiOverlap=this.query(`.apd-anti-overlap`),this.template.$syncVideo=this.query(`.apd-sync-video`),this.template.$opacitySlider=this.query(`.apd-config-opacity .apd-slider`),this.template.$opacityValue=this.query(`.apd-config-opacity .apd-value`),this.template.$marginSlider=this.query(`.apd-config-margin .apd-slider`),this.template.$marginValue=this.query(`.apd-config-margin .apd-value`),this.template.$fontSizeSlider=this.query(`.apd-config-fontSize .apd-slider`),this.template.$fontSizeValue=this.query(`.apd-config-fontSize .apd-value`),this.template.$speedSlider=this.query(`.apd-config-speed .apd-slider`),this.template.$speedValue=this.query(`.apd-config-speed .apd-value`),this.template.$input=this.query(`.apd-input`),this.template.$send=this.query(`.apd-send`);let{$toggle:r}=this.template;this.art.on(`artplayerPluginDanmuku:show`,()=>{t(r,`关闭弹幕`)}),this.art.on(`artplayerPluginDanmuku:hide`,()=>{t(r,`打开弹幕`)})}createEvents(){let{$toggle:e,$configModes:t,$styleModes:n,$colors:r,$antiOverlap:i,$syncVideo:a,$send:o,$input:s}=this.template;this.art.proxy(e,`click`,()=>{this.danmuku.config({visible:!this.option.visible}),this.reset()}),this.art.proxy(t,`click`,e=>{let t=e.target.closest(`.apd-mode`);if(!t)return;let n=Number(t.dataset.mode);this.option.modes.includes(n)?this.danmuku.config({modes:this.option.modes.filter(e=>e!==n)}):this.danmuku.config({modes:[...this.option.modes,n]}),this.reset()}),this.art.proxy(i,`click`,()=>{this.danmuku.config({antiOverlap:!this.option.antiOverlap}),this.reset()}),this.art.proxy(a,`click`,()=>{this.danmuku.config({synchronousPlayback:!this.option.synchronousPlayback}),this.reset()}),this.art.proxy(n,`click`,e=>{let t=e.target.closest(`.apd-mode`);if(!t)return;let n=Number(t.dataset.mode);this.danmuku.config({mode:n}),this.reset()}),this.art.proxy(r,`click`,e=>{let t=e.target.closest(`.apd-color`);t&&(this.danmuku.config({color:t.dataset.color}),this.reset())}),this.art.proxy(o,`click`,()=>this.emit()),this.art.proxy(s,`keypress`,e=>{e.key===`Enter`&&(e.preventDefault(),this.emit())})}createSliders(){this.slider.opacity=this.createSlider({...this.OPACITY,container:this.template.$opacitySlider,findIndex:()=>Math.round(this.option.opacity*100),onChange:e=>{let{$opacityValue:t}=this.template;t.textContent=`${e}%`,this.danmuku.config({opacity:e/100})}}),this.slider.margin=this.createSlider({...this.MARGIN,container:this.template.$marginSlider,findIndex:()=>this.MARGIN.steps.findIndex(e=>e.value[0]===this.option.margin[0]&&e.value[1]===this.option.margin[1]),onChange:e=>{let t=this.MARGIN.steps[e];if(!t)return;let{$marginValue:n}=this.template;n.textContent=t.name,this.danmuku.config({margin:t.value})}}),this.slider.fontSize=this.createSlider({...this.FONT_SIZE,container:this.template.$fontSizeSlider,findIndex:()=>this.danmuku.fontSize,onChange:e=>{let{$fontSizeValue:t}=this.template;t.textContent=`${e}px`,e!==this.danmuku.fontSize&&this.danmuku.config({fontSize:e})}}),this.slider.speed=this.createSlider({...this.SPEED,container:this.template.$speedSlider,findIndex:()=>this.SPEED.steps.findIndex(e=>e.value===this.option.speed),onChange:e=>{let t=this.SPEED.steps[e];if(!t)return;let{$speedValue:n}=this.template;n.textContent=t.name,this.danmuku.config({speed:t.value})}})}createSlider({min:e,max:t,container:n,findIndex:r,onChange:i,steps:a=[]}){let{query:o,clamp:s,setStyle:c}=this.utils;c(n,`touch-action`,`none`),n.innerHTML=`
            <div class="apd-slider-line">
                <div class="apd-slider-points">
                    ${a.map(()=>`<div class="apd-slider-point"></div>`).join(``)}
                </div>
                <div class="apd-slider-progress"></div>
            </div>
            <div class="apd-slider-dot"></div>
            <div class="apd-slider-steps">
                ${a.map(e=>e.hide?``:`<div class="apd-slider-step">${e.name}</div>`).join(``)}
            </div>
        `;let l=o(`.apd-slider-dot`,n),u=o(`.apd-slider-progress`,n),d=!1;function f(n=r()){if(n<e||n>t)return;let o=(n-e)/(t-e);l.style.left=`${o*100}%`,a.length===0&&(u.style.width=l.style.left),i(n)}function p(r){let{top:i,height:a,left:o,width:c}=n.getBoundingClientRect();if(this.art.isRotate){let n=s(r.clientY-i,0,a);f(Math.round(n/a*(t-e)+e))}else{let n=s(r.clientX-o,0,c);f(Math.round(n/c*(t-e)+e))}}return this.art.proxy(n,`click`,e=>{p.call(this,e)}),this.art.proxy(n,`pointerdown`,e=>{d=e.button===0}),this.art.on(`document:pointermove`,e=>{d&&p.call(this,e)}),this.art.on(`document:pointerup`,e=>{d&&(d=!1,p.call(this,e))}),{reset:f}}onFullscreen(e){let{$danmuku:t,$controlsCenter:n,$mount:r}=this.template;this.outside?e?this.append(n,t):this.append(r,t):this.append(n,t)}onMouseEnter({$control:e,$panel:t}){let{$player:n}=this.art.template,r=e.getBoundingClientRect(),i=t.getBoundingClientRect(),a=n.getBoundingClientRect(),o=i.width/2-r.width/2,s=a.left-(r.left-o),c=r.right+o-a.right;s>0?t.style.left=`${-o+s}px`:c>0?t.style.left=`${-o-c}px`:t.style.left=`${-o}px`}async emit(){let{$input:e}=this.template,t=e.value.trim();if(!t.length||this.isLock||this.emitting)return;let n={text:t,mode:this.option.mode,color:this.option.color,time:this.art.currentTime};try{this.emitting=!0;let t=await this.option.beforeEmit(n);if(this.emitting=!1,t!==!0)return;n.border=!0,delete n.time,this.danmuku.emit(n),e.value=``,this.lock()}catch(e){console.error(`Error emitting danmuku:`,e),this.emitting=!1}}lock(){let{addClass:e}=this.utils,{$send:t}=this.template;this.isLock=!0;let n=this.option.lockTime;t.textContent=n,e(t,`apd-lock`);let r=()=>{this.timer=setTimeout(()=>{n===0?this.unlock():(--n,t.textContent=n,r())},1e3)};r()}unlock(){let{removeClass:e}=this.utils,{$send:t}=this.template;clearTimeout(this.timer),this.isLock=!1,t.textContent=`发送`,e(t,`apd-lock`)}resize(){if(this.outside||this.art.fullscreen||this.art.fullscreenWeb)return;let{$player:e,$controlsCenter:t}=this.art.template,{$danmuku:n}=this.template;this.art.width<this.option.width?this.append(e,n):this.append(t,n)}reset(){let{inverseClass:e,tooltip:t}=this.utils,{$toggle:n,$colors:r}=this.template;this.slider.opacity.reset(),this.slider.margin.reset(),this.slider.fontSize.reset(),this.slider.speed.reset(),this.setData(`danmukuVisible`,this.option.visible),this.setData(`danmukuMode`,this.option.mode),this.setData(`danmukuColor`,this.option.color),this.setData(`danmukuMode0`,this.option.modes.includes(0)),this.setData(`danmukuMode1`,this.option.modes.includes(1)),this.setData(`danmukuMode2`,this.option.modes.includes(2)),this.setData(`danmukuAntiOverlap`,this.option.antiOverlap),this.setData(`danmukuSyncVideo`,this.option.synchronousPlayback),this.setData(`danmukuTheme`,this.option.theme),this.setData(`danmukuEmitter`,this.option.emitter);let i=r.children,a=Array.from(i).find(e=>e.dataset.color===this.option.color.toUpperCase());a&&e(a,`apd-active`),t(n,this.option.visible?`关闭弹幕`:`打开弹幕`),this.resize()}mount(e){let{errorHandle:t}=this.utils,n=typeof e==`string`?document.querySelector(e):e;t(n,`Can not find the mount point: ${e}`),this.append(n,this.template.$danmuku),this.template.$mount=n,this.reset()}},typeof document<`u`){let e=`artplayer-plugin-danmuku`,t=document.getElementById(e);t||(t=document.createElement(`style`),t.id=e,document.readyState===`loading`?document.addEventListener(`DOMContentLoaded`,()=>{document.head.appendChild(t)}):(document.head||document.documentElement).appendChild(t)),t.textContent=S}V.icons=C.icons,w=r((e,t)=>{var n=.01;typeof SubtitlesOctopusOnLoad==`function`&&SubtitlesOctopusOnLoad(),e!==void 0&&t!==void 0&&t.exports&&(e=t.exports=function(e){var t=!1;try{if(typeof WebAssembly==`object`&&typeof WebAssembly.instantiate==`function`){var r=new WebAssembly.Module(Uint8Array.of(0,97,115,109,1,0,0,0));r instanceof WebAssembly.Module&&(t=new WebAssembly.Instance(r)instanceof WebAssembly.Instance)}}catch(e){}console.log(`WebAssembly support detected: `+(t?`yes`:`no`));var i=this;i.canvas=e.canvas,i.renderMode=e.renderMode||(e.lossyRender?`lossy`:`wasm-blend`),i.dropAllAnimations=e.dropAllAnimations||!1,i.libassMemoryLimit=e.libassMemoryLimit||0,i.libassGlyphLimit=e.libassGlyphLimit||0,i.targetFps=e.targetFps||24,i.prescaleFactor=e.prescaleFactor||1,i.prescaleHeightLimit=e.prescaleHeightLimit||1080,i.maxRenderHeight=e.maxRenderHeight||0,i.resizeVariation=e.resizeVariation||.2,i.renderAhead=e.renderAhead||0,i.isOurCanvas=!1,i.video=e.video,i.canvasParent=null,i.fonts=e.fonts||[],i.availableFonts=e.availableFonts||[],i.fallbackFont=e.fallbackFont||`default.woff2`,i.lazyFileLoading=e.lazyFileLoading||!1,i.onReadyEvent=e.onReady,i.workerUrl=t?e.workerUrl||`subtitles-octopus-worker.js`:e.legacyWorkerUrl||`subtitles-octopus-worker-legacy.js`,i.subUrl=e.subUrl,i.subContent=e.subContent||null,i.onErrorEvent=e.onError,i.debug=e.debug||!1,i.lastRenderTime=0,i.pixelRatio=window.devicePixelRatio||1,i.renderedItems=[],i.renderAhead=i.renderAhead*1024*1024*.9,i.oneshotState={displayedEvent:null,eventStart:null,eventOver:!1,iteration:0,renderRequested:!1,requestNextTimestamp:-1,nextRequestOffset:0,restart:!0,prevWidth:null,prevHeight:null},i.rafId=0,i.hasAlphaBug=!1;var a,o,s=e.timeOffset||0;(function(){if(typeof ImageData.prototype.constructor==`function`)try{new window.ImageData(new Uint8ClampedArray([0,0,0,0]),1,1);return}catch(e){console.log(`detected that ImageData is not constructable despite browser saying so`)}var e=document.createElement(`canvas`).getContext(`2d`);window.ImageData=function(){var t=0;if(arguments[0]instanceof Uint8ClampedArray)var n=arguments[t++];var r=arguments[t++],i=arguments[t],a=e.createImageData(r,i);return n&&a.data.set(n),a}})(),Object.defineProperty(i,"timeOffset",{get:function(){return s},set:function(e){s!==e&&(s=e,_())}}),i.workerError=function(e){if(console.error(`Worker error: `,e),i.onErrorEvent&&i.onErrorEvent(e),!i.debug)throw i.dispose(),Error(`Worker error: `+e)},i.init=function(){if(!window.Worker){i.workerError(`worker not supported`);return}i.worker||(i.worker=new Worker(i.workerUrl),i.worker.addEventListener(`message`,i.onWorkerMessage),i.worker.addEventListener(`error`,i.workerError)),i.workerActive=!1,i.createCanvas(),i.setVideo(e.video),i.setSubUrl(e.subUrl),a=i.canvas.width,o=i.canvas.height,i.worker.postMessage({target:`worker-init`,width:i.canvas.width,height:i.canvas.height,URL:document.URL,currentScript:i.workerUrl,preMain:!0,renderMode:i.renderMode,subUrl:i.subUrl,subContent:i.subContent,fonts:i.fonts,availableFonts:i.availableFonts,fallbackFont:i.fallbackFont,lazyFileLoading:i.lazyFileLoading,debug:i.debug,targetFps:i.targetFps,libassMemoryLimit:i.libassMemoryLimit,libassGlyphLimit:i.libassGlyphLimit,renderOnDemand:i.renderAhead>0,dropAllAnimations:i.dropAllAnimations})},i.createCanvas=function(){i.canvas||(i.video?(i.isOurCanvas=!0,i.canvas=document.createElement(`canvas`),i.canvas.className=`libassjs-canvas`,i.canvas.style.display=`none`,i.canvasParent=document.createElement(`div`),i.canvasParent.className=`libassjs-canvas-parent`,i.canvasParent.appendChild(i.canvas),i.video.nextSibling?i.video.parentNode.insertBefore(i.canvasParent,i.video.nextSibling):i.video.parentNode.appendChild(i.canvasParent)):i.canvas||i.workerError(`Don't know where to render: you should give video or canvas in options.`)),i.ctx=i.canvas.getContext(`2d`),i.bufferCanvas=document.createElement(`canvas`),i.bufferCanvasCtx=i.bufferCanvas.getContext(`2d`),i.bufferCanvas.width=1,i.bufferCanvas.height=1;var e=new Uint8ClampedArray([0,255,0,0]),t=new ImageData(e,1,1);i.bufferCanvasCtx.clearRect(0,0,1,1),i.ctx.clearRect(0,0,1,1);var n=i.ctx.getImageData(0,0,1,1).data;i.bufferCanvasCtx.putImageData(t,0,0),i.ctx.drawImage(i.bufferCanvas,0,0);var r=i.ctx.getImageData(0,0,1,1).data;i.hasAlphaBug=n[1]!=r[1],i.hasAlphaBug&&console.log(`Detected a browser having issue with transparent pixels, applying workaround`)};function c(){i.setCurrentTime(i.video.currentTime+s)}function l(){i.setIsPaused(!1,i.video.currentTime+s)}function u(){i.setIsPaused(!0,i.video.currentTime+s)}function d(){i.video.removeEventListener(`timeupdate`,c,!1)}function f(){i.video.addEventListener(`timeupdate`,c,!1),_()}function p(){i.setRate(i.video.playbackRate)}function m(){i.setIsPaused(!0,i.video.currentTime+s)}function h(e){e.target.removeEventListener(e.type,h,!1),i.resize()}i.setVideo=function(e){i.video=e,i.video&&(i.video.addEventListener(`timeupdate`,c,!1),i.video.addEventListener(`playing`,l,!1),i.video.addEventListener(`pause`,u,!1),i.video.addEventListener(`seeking`,d,!1),i.video.addEventListener(`seeked`,f,!1),i.video.addEventListener(`ratechange`,p,!1),i.video.addEventListener(`waiting`,m,!1),document.addEventListener(`fullscreenchange`,i.resizeWithTimeout,!1),document.addEventListener(`mozfullscreenchange`,i.resizeWithTimeout,!1),document.addEventListener(`webkitfullscreenchange`,i.resizeWithTimeout,!1),document.addEventListener(`msfullscreenchange`,i.resizeWithTimeout,!1),window.addEventListener(`resize`,i.resizeWithTimeout,!1),typeof ResizeObserver<`u`&&(i.ro=new ResizeObserver(i.resizeWithTimeout),i.ro.observe(i.video)),i.video.videoWidth>0?i.resize():i.video.addEventListener(`loadedmetadata`,h,!1))},i.getVideoPosition=function(){var e=i.video.videoWidth/i.video.videoHeight,t=i.video.offsetWidth,n=i.video.offsetHeight,r=t/n,a=t,o=n;r>e?a=Math.floor(n*e):o=Math.floor(t/e);var s=(t-a)/2,c=(n-o)/2;return{width:a,height:o,x:s,y:c}},i.setSubUrl=function(e){i.subUrl=e};function g(e,t){for(var n=[],r=0,a=i.renderedItems.length;r<a;r++){var o=i.renderedItems[r];(o.emptyFinish<0||e<o.emptyFinish)&&n.push(o)}if(t&&n.length>0&&e<n[0].eventStart){if(n[0].eventStart-e>60)console.info(`seeked back too far, cleaning prerender buffer`),n=[];else{console.info(`seeked backwards, need to free up some buffer`);for(var s=0,c=i.renderAhead*.3,l=[],r=0,a=n.length;r<a;r++){var o=n[r];if(s+=o.size,s>=c||o.emptyFinish<0)break;l.push(o)}n=l}}t&&(i.oneshotState.displayedEvent=null,i.oneshotState.nextRequestOffset=0);var u=n.length<i.renderedItems.length;return i.renderedItems=n,u}function _(){var e=i.video.currentTime+s;i.setCurrentTime(e),i.renderAhead>0&&g(e,!0)}function v(e,t){if(!(!i.renderAhead||i.renderAhead<=0)&&(!i.oneshotState.renderRequested||t)){if(e===void 0){if(!i.video)return;e=i.video.currentTime+s}for(var n=0,r=0,a=i.renderedItems.length;r<a;r++){var o=i.renderedItems[r];if(o.emptyFinish<0){console.info(`oneshot already reached end-of-events`);return}if(e>=o.eventStart&&e<o.emptyFinish){console.debug(`not requesting a render for `+e+` as event already covering it exists (start=`+o.eventStart+`, empty=`+o.emptyFinish+`)`);return}n+=o.size}n<=i.renderAhead&&(i.oneshotState.renderRequested?(i.workerActive&&console.info(`worker busy, requesting to seek`),i.oneshotState.requestNextTimestamp=e):(i.oneshotState.renderRequested=!0,i.worker.postMessage({target:`oneshot-render`,lastRendered:e,renderNow:t,iteration:i.oneshotState.iteration})))}}function y(){i.ctx.clearRect(0,0,i.canvas.width,i.canvas.height),i.oneshotState.displayedEvent=null,i.oneshotState.eventStart=null,i.oneshotState.eventOver=!1}function b(e,t){i.oneshotState.displayedEvent=e;var n=e.eventFinish!==e.emptyFinish&&e.eventFinish<=t;if(i.oneshotState.eventStart!=e.eventStart||i.oneshotState.eventOver!=n){i.oneshotState.eventStart=e.eventStart,i.oneshotState.eventOver=n,i.oneshotState.nextRequestOffset=(i.oneshotState.nextRequestOffset+e.spentTime*.001)*.5,i.oneshotState.nextRequestOffset=Math.min(i.oneshotState.nextRequestOffset,1);var r=performance.now();if((e.viewport.width!=i.canvas.width||e.viewport.height!=i.canvas.height)&&(i.canvas.width=e.viewport.width,i.canvas.height=e.viewport.height),i.ctx.clearRect(0,0,i.canvas.width,i.canvas.height),!n)for(var a=0;a<e.items.length;a++){var o=e.items[a];i.bufferCanvas.width=o.w,i.bufferCanvas.height=o.h,i.bufferCanvasCtx.putImageData(o.image,0,0),i.ctx.drawImage(i.bufferCanvas,o.x,o.y)}if(i.debug){var s=Math.round(performance.now()-r);console.log(`render: `+Math.round(e.spentTime-e.blendTime)+` ms, blend: `+Math.round(e.blendTime)+` ms, draw: `+s+` ms`)}}}function x(){if(i.rafId=window.requestAnimationFrame(x),i.video){for(var e=i.video.currentTime+s,t=null,r=0,a=i.renderedItems.length;r<a;r++){var o=i.renderedItems[r];if(o.eventStart<=e)t=o;else break}t?b(t,e):i.oneshotState.displayedEvent?b(i.oneshotState.displayedEvent,e):y();var c=e;i.video.paused||(c+=Math.max(i.oneshotState.nextRequestOffset,1/i.targetFps)*i.video.playbackRate),c<0&&(c=0);for(var l=null,u=-1,d=!1,r=0,a=i.renderedItems.length;r<a;r++){var o=i.renderedItems[r];if(o.eventStart<=c)l=o,u=o.emptyFinish;else if(u>=0){if(o.eventStart-u<n)u=o.emptyFinish,d=o.animated;else break}else break}l&&(c=u>=0?Math.max(u,c):-1),(!i.video.paused&&g(e)||!t||i.oneshotState.restart)&&c>=0&&Math.abs(i.oneshotState.requestNextTimestamp-c)>n&&v(c,c!==u||d),i.oneshotState.restart=!1}}function S(){window.cancelAnimationFrame(i.rafId),i.rafId=0}i.resetRenderAheadCache=function(e){if(i.renderAhead>0){var t=[];if(e&&i.oneshotState.prevHeight&&i.oneshotState.prevWidth){if(i.oneshotState.prevHeight===o&&i.oneshotState.prevWidth===a)return;var n=10,r=i.renderAhead*.3;o>=i.oneshotState.prevHeight*(1-i.resizeVariation)&&o<=i.oneshotState.prevHeight*(1+i.resizeVariation)&&a>=i.oneshotState.prevWidth*(1-i.resizeVariation)&&a<=i.oneshotState.prevWidth*(1+i.resizeVariation)&&(console.debug(`viewport changes are small, leaving more of prerendered buffer`),n=30,r=i.renderAhead*.5);for(var c=i.video.currentTime+s+n,l=0,u=0;u<i.renderedItems.length;u++){var d=i.renderedItems[u];if(d.emptyFinish<0||c<d.eventFinish||(l+=d.size,l>=r))break;t.push(d)}}console.info(`resetting prerender cache`),i.renderedItems=t,i.oneshotState.eventStart=null,i.oneshotState.iteration++,i.oneshotState.renderRequested=!1,i.oneshotState.prevHeight=o,i.oneshotState.prevWidth=a,i.oneshotState.nextRequestOffset=0,i.oneshotState.restart=!0,e||(i.oneshotState.displayedEvent=null),i.rafId||(i.rafId=window.requestAnimationFrame(x)),v(void 0,!0)}},i.renderFrameData=null;function C(){var e=i.renderFramesData,t=performance.now();i.ctx.clearRect(0,0,i.canvas.width,i.canvas.height);for(var n=0;n<e.canvases.length;n++){var r=e.canvases[n];i.bufferCanvas.width=r.w,i.bufferCanvas.height=r.h;var a=new Uint8ClampedArray(r.buffer);if(i.hasAlphaBug)for(var o=3;o<a.length;o+=4)a[o]=a[o]>=1?a[o]:1;var s=new ImageData(a,r.w,r.h);i.bufferCanvasCtx.putImageData(s,0,0),i.ctx.drawImage(i.bufferCanvas,r.x,r.y)}if(i.debug){var c=Math.round(performance.now()-t),l=e.blendTime;console.log(l===void 0?Math.round(e.spentTime)+` ms (+ `+c+` ms draw)`:`render: `+Math.round(e.spentTime-l)+` ms, blend: `+Math.round(l)+` ms, draw: `+c+` ms; TOTAL=`+Math.round(e.spentTime+c)+` ms`),i.renderStart=performance.now()}}function w(){var e=i.renderFramesData,t=performance.now();i.ctx.clearRect(0,0,i.canvas.width,i.canvas.height);for(var n=0;n<e.bitmaps.length;n++){var r=e.bitmaps[n];i.ctx.drawImage(r.bitmap,r.x,r.y)}if(i.debug){var a=Math.round(performance.now()-t);console.log(e.bitmaps.length+` bitmaps, libass: `+Math.round(e.libassTime)+`ms, decode: `+Math.round(e.decodeTime)+`ms, draw: `+a+`ms`),i.renderStart=performance.now()}}i.workerActive=!1,i.frameId=0,i.onWorkerMessage=function(e){i.workerActive||(i.workerActive=!0,i.onReadyEvent&&i.onReadyEvent());var t=e.data;switch(t.target){case`stdout`:console.log(t.content);break;case`console-log`:console.log.apply(console,JSON.parse(t.content));break;case`console-debug`:console.debug.apply(console,JSON.parse(t.content));break;case`console-info`:console.info.apply(console,JSON.parse(t.content));break;case`console-warn`:console.warn.apply(console,JSON.parse(t.content));break;case`console-error`:console.error.apply(console,JSON.parse(t.content));break;case`stderr`:console.error(t.content);break;case`window`:window[t.method]();break;case`canvas`:switch(t.op){case`getContext`:i.ctx=i.canvas.getContext(t.type,t.attributes);break;case`resize`:i.resize(t.width,t.height);break;case`renderCanvas`:i.lastRenderTime<t.time&&(i.lastRenderTime=t.time,i.renderFramesData=t,window.requestAnimationFrame(C));break;case`renderFastCanvas`:i.lastRenderTime<t.time&&(i.lastRenderTime=t.time,i.renderFramesData=t,window.requestAnimationFrame(w));break;case`setObjectProperty`:i.canvas[t.object][t.property]=t.value;break;case`oneshot-result`:if(t.iteration!=i.oneshotState.iteration){console.debug(`received stale prerender, ignoring`);return}i.debug&&console.info(`oneshot received (start=`+t.eventStart+`, empty=`+t.emptyFinish+`), render: `+Math.round(t.spentTime)+` ms`);var r=i.oneshotState.requestNextTimestamp;i.oneshotState.renderRequested=!1,i.oneshotState.requestNextTimestamp=-1,Math.abs(t.lastRenderedTime-r)<n&&(r=-1),t.eventStart-t.lastRenderedTime>n&&i.renderedItems.push({eventStart:t.lastRenderedTime,eventFinish:t.lastRenderedTime-.001,emptyFinish:t.eventStart,viewport:t.viewport,spentTime:0,blendTime:0,items:[],animated:!1,size:0});for(var a=[],o=0,s=0,c=t.canvases.length;s<c;s++){var l=t.canvases[s];a.push({w:l.w,h:l.h,x:l.x,y:l.y,image:new ImageData(new Uint8ClampedArray(l.buffer),l.w,l.h)}),o+=l.buffer.byteLength}var u=!1;if(t.emptyFinish>0&&t.emptyFinish-t.eventStart<1/i.targetFps||t.animated){var d=t.eventStart+1/i.targetFps;t.emptyFinish=d,t.eventFinish=d,u=!0}i.renderedItems.push({eventStart:t.eventStart,eventFinish:t.eventFinish,emptyFinish:t.emptyFinish,spentTime:t.spentTime,blendTime:t.blendTime,viewport:t.viewport,items:a,animated:t.animated,size:o}),i.renderedItems.sort(function(e,t){return e.eventStart-t.eventStart}),r>=0?v(r,!0):t.eventStart<0?console.info(`oneshot received "end of frames" event`):t.emptyFinish>=0?v(t.emptyFinish,u):console.info(`there are no more events to prerender`);break;default:throw`eh?`}break;case`tick`:i.frameId=t.id,i.worker.postMessage({target:`tock`,id:i.frameId});break;case`custom`:if(i.onCustomMessage)i.onCustomMessage(e);else throw`Custom message received but client onCustomMessage not implemented.`;break;case`setimmediate`:i.worker.postMessage({target:`setimmediate`});break;case`get-events`:break;case`get-styles`:break;case`ready`:break;default:throw`what? `+t.target}};function T(e,t){var n=i.prescaleFactor<=0?1:i.prescaleFactor;if(t<=0||e<=0)e=0,t=0;else{var r=n<1?-1:1,a=t;r*a*n<=r*i.prescaleHeightLimit?a*=n:r*a<r*i.prescaleHeightLimit&&(a=i.prescaleHeightLimit),i.maxRenderHeight>0&&a>i.maxRenderHeight&&(a=i.maxRenderHeight),e*=a/t,t=a}return{width:e,height:t}}i.resize=function(e,t,n,r){var s=null;if(n=n||0,r=r||0,(!e||!t)&&i.video){s=i.getVideoPosition();var c=T(s.width*i.pixelRatio,s.height*i.pixelRatio);e=c.width,t=c.height;var l=i.canvasParent.getBoundingClientRect().top-i.video.getBoundingClientRect().top;n=s.y-l,r=s.x}if(!e||!t){i.video||console.error(`width or height is 0. You should specify width & height for resize.`);return}s!=null&&(i.canvasParent.style.position=`relative`,i.canvas.style.display=`block`,i.canvas.style.position=`absolute`,i.canvas.style.width=s.width+`px`,i.canvas.style.height=s.height+`px`,i.canvas.style.top=n+`px`,i.canvas.style.left=r+`px`,i.canvas.style.pointerEvents=`none`),(a!==e||o!==t)&&(i.canvas.width=e,i.canvas.height=t,a=e,o=t,i.worker.postMessage({target:`canvas`,width:i.canvas.width,height:i.canvas.height}),i.resetRenderAheadCache(!0))},i.resizeWithTimeout=function(){i.resize(),setTimeout(i.resize,100)},i.runBenchmark=function(){i.worker.postMessage({target:`runBenchmark`})},i.customMessage=function(e,t){t=t||{},i.worker.postMessage({target:`custom`,userData:e,preMain:t.preMain})},i.setCurrentTime=function(e){i.worker.postMessage({target:`video`,currentTime:e})},i.setTrackByUrl=function(e){i.worker.postMessage({target:`set-track-by-url`,url:e}),i.resetRenderAheadCache(!1)},i.setTrack=function(e){i.worker.postMessage({target:`set-track`,content:e}),i.resetRenderAheadCache(!1)},i.freeTrack=function(e){i.worker.postMessage({target:`free-track`}),i.resetRenderAheadCache(!1)},i.render=i.setCurrentTime,i.setIsPaused=function(e,t){i.worker.postMessage({target:`video`,isPaused:e,currentTime:t})},i.setRate=function(e){i.worker.postMessage({target:`video`,rate:e})},i.dispose=function(){i.worker.postMessage({target:`destroy`}),i.worker.terminate(),i.worker.removeEventListener(`message`,i.onWorkerMessage),i.worker.removeEventListener(`error`,i.workerError),i.workerActive=!1,i.worker=null,i.video&&(i.video.removeEventListener(`timeupdate`,c,!1),i.video.removeEventListener(`playing`,l,!1),i.video.removeEventListener(`pause`,u,!1),i.video.removeEventListener(`seeking`,d,!1),i.video.removeEventListener(`seeked`,f,!1),i.video.removeEventListener(`ratechange`,p,!1),i.video.removeEventListener(`waiting`,m,!1),i.video.removeEventListener(`loadedmetadata`,h,!1),document.removeEventListener(`fullscreenchange`,i.resizeWithTimeout,!1),document.removeEventListener(`mozfullscreenchange`,i.resizeWithTimeout,!1),document.removeEventListener(`webkitfullscreenchange`,i.resizeWithTimeout,!1),document.removeEventListener(`msfullscreenchange`,i.resizeWithTimeout,!1),window.removeEventListener(`resize`,i.resizeWithTimeout,!1),i.video.parentNode.removeChild(i.canvasParent),i.video=null),S(),i.ro&&(i.ro.disconnect(),i.ro=null),i.onCustomMessage=null,i.onErrorEvent=null,i.onReadyEvent=null},i.fetchFromWorker=function(e,t,n){try{var r=e.target,a=setTimeout(function(){s(Error(`Error: Timeout while try to fetch `+r))},5e3),o=function(e){e.data.target==r&&(t(e.data),i.worker.removeEventListener(`message`,o),i.worker.removeEventListener(`error`,s),clearTimeout(a))},s=function(e){n(e),i.worker.removeEventListener(`message`,o),i.worker.removeEventListener(`error`,s),clearTimeout(a)};i.worker.addEventListener(`message`,o),i.worker.addEventListener(`error`,s),i.worker.postMessage(e)}catch(e){n(e)}},i.createEvent=function(e){i.worker.postMessage({target:`create-event`,event:e})},i.getEvents=function(e,t){i.fetchFromWorker({target:`get-events`},function(t){e(t.events)},t)},i.setEvent=function(e,t){i.worker.postMessage({target:`set-event`,event:e,index:t})},i.removeEvent=function(e){i.worker.postMessage({target:`remove-event`,index:e})},i.createStyle=function(e){i.worker.postMessage({target:`create-style`,style:e})},i.getStyles=function(e,t){i.fetchFromWorker({target:`get-styles`},function(t){e(t.styles)},t)},i.setStyle=function(e,t){i.worker.postMessage({target:`set-style`,style:e,index:t})},i.removeStyle=function(e){i.worker.postMessage({target:`remove-style`,index:e})},i.init()})}),T=n(w(),1),{fontsPath:E,libAssPath:D}=i(),O=`${D()}/subtitles-octopus-worker.js`,k=`${D()}/subtitles-octopus-worker.wasm`,A=`${E()}/TimesNewRoman.ttf`,j=`${E()}/SourceHanSansCN-Bold.woff2`,M=null}}})})();