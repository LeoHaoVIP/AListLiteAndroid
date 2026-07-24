(function(){System.register([`./store-legacy-B6UN3gNC.js`,`./components-legacy-DBGZNfUr.js`,`./obj-legacy-3LtCUuiz.js`],function(e,t){var n,r,i,a,o,s,c,l,u,d,f,p,m,h,g,_,ee,te,ne,re,ie,ae,oe,se,ce,le,ue,de,v,fe,pe,y,b,x,S,C,w,me,T,E,he,D,ge,_e,ve,O,k,A,j,M,N,ye,P,be,xe,Se,Ce,we,Te,Ee,De,Oe,ke,Ae,je,Me,Ne,Pe,Fe,Ie,Le,Re,ze,Be,Ve,He,Ue,We,Ge,Ke,qe,Je,Ye,Xe,Ze,Qe,$e,et,tt,nt,rt,it,at,F,ot,I,st,ct,lt,ut,dt,ft,pt,mt,ht,L,R,gt;function _t(e){return e&&e.__esModule&&Object.prototype.hasOwnProperty.call(e,`default`)?e.default:e}function vt(){return pe?v.exports:(pe=1,(function(e,t){(function(t,n){e.exports=n()})(fe,function(){function e(t){return(e=typeof Symbol==`function`&&typeof Symbol.iterator==`symbol`?function(e){return typeof e}:function(e){return e&&typeof Symbol==`function`&&e.constructor===Symbol&&e!==Symbol.prototype?`symbol`:typeof e})(t)}var t=Object.prototype.toString,n=function(n){if(n===void 0)return`undefined`;if(n===null)return`null`;var i=e(n);if(i===`boolean`)return`boolean`;if(i===`string`)return`string`;if(i===`number`)return`number`;if(i===`symbol`)return`symbol`;if(i===`function`)return function(e){return r(e)===`GeneratorFunction`}(n)?`generatorfunction`:`function`;if(function(e){return Array.isArray?Array.isArray(e):e instanceof Array}(n))return`array`;if(function(e){return e.constructor&&typeof e.constructor.isBuffer==`function`?e.constructor.isBuffer(e):!1}(n))return`buffer`;if(function(e){try{if(typeof e.length==`number`&&typeof e.callee==`function`)return!0}catch(e){if(e.message.indexOf(`callee`)!==-1)return!0}return!1}(n))return`arguments`;if(function(e){return e instanceof Date||typeof e.toDateString==`function`&&typeof e.getDate==`function`&&typeof e.setDate==`function`}(n))return`date`;if(function(e){return e instanceof Error||typeof e.message==`string`&&e.constructor&&typeof e.constructor.stackTraceLimit==`number`}(n))return`error`;if(function(e){return e instanceof RegExp||typeof e.flags==`string`&&typeof e.ignoreCase==`boolean`&&typeof e.multiline==`boolean`&&typeof e.global==`boolean`}(n))return`regexp`;switch(r(n)){case`Symbol`:return`symbol`;case`Promise`:return`promise`;case`WeakMap`:return`weakmap`;case`WeakSet`:return`weakset`;case`Map`:return`map`;case`Set`:return`set`;case`Int8Array`:return`int8array`;case`Uint8Array`:return`uint8array`;case`Uint8ClampedArray`:return`uint8clampedarray`;case`Int16Array`:return`int16array`;case`Uint16Array`:return`uint16array`;case`Int32Array`:return`int32array`;case`Uint32Array`:return`uint32array`;case`Float32Array`:return`float32array`;case`Float64Array`:return`float64array`}if(function(e){return typeof e.throw==`function`&&typeof e.return==`function`&&typeof e.next==`function`}(n))return`generator`;switch(i=t.call(n)){case`[object Object]`:return`object`;case`[object Map Iterator]`:return`mapiterator`;case`[object Set Iterator]`:return`setiterator`;case`[object String Iterator]`:return`stringiterator`;case`[object Array Iterator]`:return`arrayiterator`}return i.slice(8,-1).toLowerCase().replace(/\s/g,``)};function r(e){return e.constructor?e.constructor.name:null}function i(e,t){var r=2<arguments.length&&arguments[2]!==void 0?arguments[2]:[`option`];return a(e,t,r),o(e,t,r),function(e,t,r){var s=n(t),c=n(e);if(s===`object`){if(c!==`object`)throw Error(`[Type Error]: '${r.join(`.`)}' require 'object' type, but got '${c}'`);Object.keys(t).forEach(function(n){var s=e[n],c=t[n],l=r.slice();l.push(n),a(s,c,l),o(s,c,l),i(s,c,l)})}if(s===`array`){if(c!==`array`)throw Error(`[Type Error]: '${r.join(`.`)}' require 'array' type, but got '${c}'`);e.forEach(function(n,s){var c=e[s],l=t[s]||t[0],u=r.slice();u.push(s),a(c,l,u),o(c,l,u),i(c,l,u)})}}(e,t,r),e}function a(e,t,r){if(n(t)===`string`){var i=n(e);if(t[0]===`?`&&(t=t.slice(1)+`|undefined`),!(-1<t.indexOf(`|`)?t.split(`|`).map(function(e){return e.toLowerCase().trim()}).filter(Boolean).some(function(e){return i===e}):t.toLowerCase().trim()===i))throw Error(`[Type Error]: '${r.join(`.`)}' require '${t}' type, but got '${i}'`)}}function o(e,t,r){if(n(t)===`function`){var i=t(e,n(e),r);if(!0!==i){var a=n(i);throw a===`string`?Error(i):a===`error`?i:Error(`[Validator Error]: The scheme for '${r.join(`.`)}' validator require return true, but got '${i}'`)}}}return i.kindOf=n,i})})(v),v.exports)}function z(e,t=document){return t.querySelector(e)}function yt(e,t=document){return Array.from(t.querySelectorAll(e))}function B(e,t){return e.classList.add(t)}function V(e,t){return e.classList.remove(t)}function H(e,t){return e.classList.contains(t)}function U(e,t){return t instanceof Element?e.appendChild(t):e.insertAdjacentHTML(`beforeend`,String(t)),e.lastElementChild||e.lastChild}function bt(e){return e.parentNode.removeChild(e)}function W(e,t,n){return e.style[t]=n,e}function xt(e,t){for(let n in t)W(e,n,t[n]);return e}function St(e,t,n=!0){let r=window.getComputedStyle(e,null).getPropertyValue(t);return n?Number.parseFloat(r):r}function Ct(e){return Array.from(e.parentElement.children).filter(t=>t!==e)}function G(e,t){Ct(e).forEach(e=>V(e,t)),B(e,t)}function K(e,t,n=`top`){T||(e.setAttribute(`aria-label`,t),B(e,`hint--rounded`),B(e,`hint--${n}`))}function wt(e,t=0){let n=e.getBoundingClientRect(),r=window.innerHeight||document.documentElement.clientHeight,i=window.innerWidth||document.documentElement.clientWidth,a=n.top-t<=r&&n.top+n.height+t>=0,o=n.left-t<=i+t&&n.left+n.width+t>=0;return a&&o}function q(e,t){return At(e).includes(t)}function Tt(e,t){return t.parentNode.replaceChild(e,t),e}function J(e){return document.createElement(e)}function Et(e=``,t=``){let n=J(`i`);return B(n,`art-icon`),B(n,`art-icon-${e}`),U(n,t),n}function Dt(e,t){let n=document.getElementById(e);n||(n=document.createElement(`style`),n.id=e,document.readyState===`loading`?document.addEventListener(`DOMContentLoaded`,()=>{document.head.appendChild(n)}):(document.head||document.documentElement).appendChild(n)),n.textContent=t}function Ot(){let e=document.createElement(`div`);return e.style.display=`flex`,e.style.display===`flex`}function Y(e){return e.getBoundingClientRect()}function kt(e,t){return new Promise((n,r)=>{let i=new Image;i.onload=function(){if(!t||t===1)n(i);else{let a=document.createElement(`canvas`),o=a.getContext(`2d`);a.width=i.width*t,a.height=i.height*t,o.drawImage(i,0,0,a.width,a.height),a.toBlob(t=>{let i=URL.createObjectURL(t),a=new Image;a.onload=function(){n(a)},a.onerror=function(){URL.revokeObjectURL(i),r(Error(`Image load failed: ${e}`))},a.src=i})}},i.onerror=function(){r(Error(`Image load failed: ${e}`))},i.src=e})}function At(e){if(e.composedPath)return e.composedPath();let t=[],n=e.target;for(;n;)t.push(n),n=n.parentNode;return!t.includes(window)&&window!==void 0&&t.push(window),t}function X(e,t){if(!e)throw new he(t);return e}function jt(e){return e.includes(`?`)?jt(e.split(`?`)[0]):e.includes(`#`)?jt(e.split(`#`)[0]):e.trim().toLowerCase().split(`.`).pop()}function Mt(e,t){let n=document.createElement(`a`);n.style.display=`none`,n.href=e,n.download=t,document.body.appendChild(n),n.click(),document.body.removeChild(n)}function Z(e,t,n){return Math.max(Math.min(e,Math.max(t,n)),Math.min(t,n))}function Nt(e){return e.charAt(0).toUpperCase()+e.slice(1)}function Q(e){if(!e)return`00:00`;let t=e=>e<10?`0${e}`:String(e),n=Math.floor(e/3600),r=Math.floor((e-n*3600)/60),i=Math.floor(e-n*3600-r*60);return(n>0?[n,r,i]:[r,i]).map(t).join(`:`)}function Pt(e){return e.replace(/[&<>'"]/g,e=>({"&":`&amp;`,"<":`&lt;`,">":`&gt;`,"'":`&#39;`,'"':`&quot;`})[e]||e)}function Ft(e){let t={"&amp;":`&`,"&lt;":`<`,"&gt;":`>`,"&#39;":`'`,"&quot;":`"`},n=RegExp(`(${Object.keys(t).join(`|`)})`,`g`);return e.replace(n,e=>t[e]||e)}function It(e,t){return ge.call(e,t)}function Lt(e,t){return Object.getOwnPropertyDescriptor(e,t)}function Rt(...e){let t=e=>e&&typeof e==`object`&&!Array.isArray(e);return e.reduce((e,n)=>(Object.keys(n).forEach(r=>{let i=e[r],a=n[r];Array.isArray(i)&&Array.isArray(a)?e[r]=i.concat(...a):t(i)&&t(a)?e[r]=Rt(i,a):e[r]=a}),e),{})}function zt(e){return e.replace(/(\d\d:\d\d:\d\d)[,.](\d+)/g,(e,t,n)=>{let r=n.slice(0,3);return n.length===1&&(r=`${n}00`),n.length===2&&(r=`${n}0`),`${t},${r}`})}function Bt(e){return`WEBVTT \r
\r
${zt(e).replace(/\{\\([ibu])\}/g,`</$1>`).replace(/\{\\([ibu])1\}/g,`<$1>`).replace(/\{([ibu])\}/g,`<$1>`).replace(/\{\/([ibu])\}/g,`</$1>`).replace(/(\d\d:\d\d:\d\d),(\d\d\d)/g,`$1.$2`).replace(/\{[\s\S]*?\}/g,``).concat(`\r
\r
`)}`}function Vt(e){return URL.createObjectURL(new Blob([e],{type:`text/vtt`}))}function Ht(e){let t=RegExp(`Dialogue:\\s\\d,(\\d+:\\d\\d:\\d\\d.\\d\\d),(\\d+:\\d\\d:\\d\\d.\\d\\d),([^,]*),([^,]*),(?:[^,]*,){4}([\\s\\S]*)$`,`i`);function n(e=``){return e.split(/[:.]/).map((e,t,n)=>{if(t===n.length-1){if(e.length===1)return`.${e}00`;if(e.length===2)return`.${e}0`}else if(e.length===1)return(t===0?`0`:`:0`)+e;return t===0?e:t===n.length-1?`.${e}`:`:${e}`}).join(``)}return`WEBVTT

${e.split(/\r?\n/).map(e=>{let r=e.match(t);return r?{start:n(r[1].trim()),end:n(r[2].trim()),text:r[5].replace(/\{[\s\S]*?\}/g,``).replace(/(\\N)/g,`
`).trim().split(/\r?\n/).map(e=>e.trim()).join(`
`)}:null}).filter(e=>e).map((e,t)=>e?`${t+1}
${e.start} --> ${e.end}
${e.text}`:``).filter(e=>e.trim()).join(`

`)}`}function $(e=0){return new Promise(t=>setTimeout(t,e))}function Ut(e,t){let n;return function(...r){clearTimeout(n),n=setTimeout(()=>(n=null,e.apply(this,r)),t)}}function Wt(e,t){let n=!1;return function(...r){n||(e.apply(this,r),n=!0,setTimeout(()=>{n=!1},t))}}function Gt(e,t,n){return X(t===k||t===A||e instanceof Element,`${n.join(`.`)} require '${k}' or 'Element' type`)}function Kt(e){return t=>{let{i18n:n,constructor:{ASPECT_RATIO:r}}=t,i=r.map(e=>`<span data-value="${e}">${e==="default"?n.get(`Default`):e}</span>`).join(``);return{...e,html:`${n.get(`Aspect Ratio`)}: ${i}`,click:(e,n)=>{let{value:r}=n.target.dataset;r&&(t.aspectRatio=r,e.show=!1)},mounted:e=>{let n=z(`[data-value="default"]`,e);n&&G(n,`art-current`),t.on(`aspectRatio`,t=>{let n=yt(`span`,e).find(e=>e.dataset.value===t);n&&G(n,`art-current`)})}}}}function qt(e){return t=>({...e,html:t.i18n.get(`Close`),click:e=>{e.show=!1}})}function Jt(e){return t=>{let{i18n:n,constructor:{FLIP:r}}=t,i=r.map(e=>`<span data-value="${e}">${n.get(Nt(e))}</span>`).join(``);return{...e,html:`${n.get(`Video Flip`)}: ${i}`,click:(e,n)=>{let{value:r}=n.target.dataset;r&&(t.flip=r.toLowerCase(),e.show=!1)},mounted:e=>{let n=z(`[data-value="normal"]`,e);n&&G(n,`art-current`),t.on(`flip`,t=>{let n=yt(`span`,e).find(e=>e.dataset.value===t);n&&G(n,`art-current`)})}}}}function Yt(e){return t=>({...e,html:t.i18n.get(`Video Info`),click:e=>{t.info.show=!0,e.show=!1}})}function Xt(e){return t=>{let{i18n:n,constructor:{PLAYBACK_RATE:r}}=t,i=r.map(e=>`<span data-value="${e}">${e===1?n.get(`Normal`):e.toFixed(1)}</span>`).join(``);return{...e,html:`${n.get(`Play Speed`)}: ${i}`,click:(e,n)=>{let{value:r}=n.target.dataset;r&&(t.playbackRate=Number(r),e.show=!1)},mounted:e=>{let n=z(`[data-value="1"]`,e);n&&G(n,`art-current`),t.on(`video:ratechange`,()=>{let n=yt(`span`,e).find(e=>Number(e.dataset.value)===t.playbackRate);n&&G(n,`art-current`)})}}}}function Zt(e){let t=E?location.href:``;return{...e,html:`<a href="https://artplayer.org?ref=${encodeURIComponent(t)}" target="_blank" style="width:100%;">ArtPlayer ${b}</a>`}}function Qt(e){return t=>({...e,tooltip:t.i18n.get(`AirPlay`),mounted:e=>{let{proxy:n,icons:r}=t;U(e,r.airplay),n(e,`click`,()=>t.airplay())}})}function $t(e){return t=>({...e,tooltip:t.i18n.get(`Fullscreen`),mounted:e=>{let{proxy:n,icons:r,i18n:i}=t,a=U(e,r.fullscreenOn),o=U(e,r.fullscreenOff);W(o,`display`,`none`),n(e,`click`,()=>{t.fullscreen=!t.fullscreen}),t.on(`fullscreen`,t=>{t?(K(e,i.get(`Exit Fullscreen`)),W(a,`display`,`none`),W(o,`display`,`inline-flex`)):(K(e,i.get(`Fullscreen`)),W(a,`display`,`inline-flex`),W(o,`display`,`none`))})}})}function en(e){return t=>({...e,tooltip:t.i18n.get(`Web Fullscreen`),mounted:e=>{let{proxy:n,icons:r,i18n:i}=t,a=U(e,r.fullscreenWebOn),o=U(e,r.fullscreenWebOff);W(o,`display`,`none`),n(e,`click`,()=>{t.fullscreenWeb=!t.fullscreenWeb}),t.on(`fullscreenWeb`,t=>{t?(K(e,i.get(`Exit Web Fullscreen`)),W(a,`display`,`none`),W(o,`display`,`inline-flex`)):(K(e,i.get(`Web Fullscreen`)),W(a,`display`,`inline-flex`),W(o,`display`,`none`))})}})}function tn(e){return t=>({...e,tooltip:t.i18n.get(`PIP Mode`),mounted:e=>{let{proxy:n,icons:r,i18n:i}=t;U(e,r.pip),n(e,`click`,()=>{t.pip=!t.pip}),t.on(`pip`,t=>{K(e,i.get(t?`Exit PIP Mode`:`PIP Mode`))})}})}function nn(e){return t=>({...e,mounted:e=>{let{proxy:n,icons:r,i18n:i}=t,a=U(e,r.play),o=U(e,r.pause);K(a,i.get(`Play`)),K(o,i.get(`Pause`)),n(a,`click`,()=>{t.play()}),n(o,`click`,()=>{t.pause()});function s(){W(a,`display`,`flex`),W(o,`display`,`none`)}function c(){W(a,`display`,`none`),W(o,`display`,`flex`)}t.playing?c():s(),t.on(`video:playing`,()=>{c()}),t.on(`video:pause`,()=>{s()})}})}function rn(e,t){let{$progress:n}=e.template,{left:r}=Y(n),i=Z((T?t.touches[0].clientX:t.clientX)-r,0,n.clientWidth),a=i/n.clientWidth*e.duration;return{second:a,time:Q(a),width:i,percentage:Z(i/n.clientWidth,0,1)}}function an(e,t){if(e.isRotate){let n=t.touches[0].clientY/e.height,r=n*e.duration;e.emit(`setBar`,`played`,n,t),e.seek=r}else{let{second:n,percentage:r}=rn(e,t);e.emit(`setBar`,`played`,r,t),e.seek=n}}function on(e){return t=>{let{icons:n,option:r,proxy:i}=t,{$player:a,$progress:o}=t.template;return{...e,html:`
                <div class="art-control-progress-inner">
                    <div class="art-progress-hover"></div>
                    <div class="art-progress-loaded"></div>
                    <div class="art-progress-played"></div>
                    <div class="art-progress-highlight"></div>
                    <div class="art-progress-indicator"></div>
                    <div class="art-progress-tip">00:00</div>
                </div>
            `,mounted:e=>{let s=null,c=!1,l=z(`.art-progress-hover`,e),u=z(`.art-progress-loaded`,e),d=z(`.art-progress-played`,e),f=z(`.art-progress-highlight`,e),p=z(`.art-progress-indicator`,e),m=z(`.art-progress-tip`,e);n.indicator?U(p,n.indicator):W(p,`backgroundColor`,`var(--art-theme)`);function h(n){let{width:r}=rn(t,n),{text:i}=n.target.dataset;m.textContent=i;let a=m.clientWidth;r<=a/2?W(m,`left`,0):r>e.clientWidth-a/2?W(m,`left`,`${e.clientWidth-a}px`):W(m,`left`,`${r-a/2}px`)}function g(n,r){let{width:i,time:a}=r||rn(t,n);m.textContent=a||`00:00`;let o=m.clientWidth;i<=o/2?W(m,`left`,0):i>e.clientWidth-o/2?W(m,`left`,`${e.clientWidth-o}px`):W(m,`left`,`${i-o/2}px`)}function _(){f.textContent=``;for(let e=0;e<r.highlight.length;e++){let n=r.highlight[e],i=Z(n.time,0,t.duration)/t.duration*100,a=`<span data-text="${n.text}" data-time="${n.time}" style="left: ${i}%"></span>`;U(f,a)}}function ee(n,r,i){let o=n===`played`&&i&&T;n===`loaded`&&W(u,`width`,`${r*100}%`),n===`hover`&&(W(l,`width`,`${r*100}%`),q(i,f)?h(i):g(i),r===0?V(a,`art-progress-hover`):B(a,`art-progress-hover`)),n===`played`&&(W(d,`width`,`${r*100}%`),W(p,`left`,`${r*100}%`)),o&&(B(a,`art-progress-hover`),g(i,{width:e.clientWidth*r,time:Q(r*t.duration)}),clearTimeout(s),s=setTimeout(()=>{V(a,`art-progress-hover`)},500))}t.on(`setBar`,ee),t.on(`video:loadedmetadata`,_),t.constructor.USE_RAF?t.on(`raf`,()=>{t.emit(`setBar`,`played`,t.played),t.emit(`setBar`,`loaded`,t.loaded)}):(t.on(`video:timeupdate`,()=>{t.emit(`setBar`,`played`,t.played)}),t.on(`video:progress`,()=>{t.emit(`setBar`,`loaded`,t.loaded)}),t.on(`video:ended`,()=>{t.emit(`setBar`,`played`,1)})),t.emit(`setBar`,`loaded`,t.loaded||0),T||(i(o,`click`,e=>{e.target!==p&&an(t,e)}),i(o,`mousemove`,e=>{let{percentage:n}=rn(t,e);t.emit(`setBar`,`hover`,n,e)}),i(o,`mouseleave`,e=>{t.emit(`setBar`,`hover`,0,e)}),i(o,`mousedown`,e=>{c=e.button===0}),t.on(`document:mousemove`,e=>{if(c){let{second:n,percentage:r}=rn(t,e);t.emit(`setBar`,`played`,r,e),t.seek=n}}),t.on(`document:mouseup`,()=>{c&&(c=!1)}))}}}}function sn(e){return t=>({...e,tooltip:t.i18n.get(`Screenshot`),mounted:e=>{let{proxy:n,icons:r}=t;U(e,r.screenshot),n(e,`click`,()=>{t.screenshot()})}})}function cn(e){return t=>({...e,tooltip:t.i18n.get(`Show Setting`),mounted:e=>{let{proxy:n,icons:r,i18n:i}=t;U(e,r.setting),n(e,`click`,()=>{t.setting.toggle(),t.setting.resize()}),t.on(`setting`,t=>{K(e,i.get(t?`Hide Setting`:`Show Setting`))})}})}function ln(e){return t=>({...e,style:T?{fontSize:`12px`,padding:`0 5px`}:{cursor:`auto`,padding:`0 10px`},mounted:e=>{function n(){let n=`${Q(t.currentTime)} / ${Q(t.duration)}`;n!==e.textContent&&(e.textContent=n)}n();let r=[`video:loadedmetadata`,`video:timeupdate`,`video:progress`];for(let e=0;e<r.length;e++)t.on(r[e],n)}})}function un(e){return t=>({...e,mounted:e=>{let{proxy:n,icons:r}=t,i=U(e,r.volume),a=U(e,r.volumeClose),o=U(e,`<div class="art-volume-panel"></div>`),s=U(o,`<div class="art-volume-inner"></div>`),c=U(s,`<div class="art-volume-val"></div>`),l=U(s,`<div class="art-volume-slider"></div>`),u=U(U(l,`<div class="art-volume-handle"></div>`),`<div class="art-volume-loaded"></div>`),d=U(l,`<div class="art-volume-indicator"></div>`);function f(e){let{top:t,height:n}=Y(l);return 1-(e.clientY-t)/n}function p(){if(t.muted||t.volume===0)W(i,`display`,`none`),W(a,`display`,`flex`),W(d,`top`,`100%`),W(u,`top`,`100%`),c.textContent=0;else{let e=t.volume*100;W(i,`display`,`flex`),W(a,`display`,`none`),W(d,`top`,`${100-e}%`),W(u,`top`,`${100-e}%`),c.textContent=Math.floor(e)}}if(p(),t.on(`video:volumechange`,p),n(i,`click`,()=>{t.muted=!0}),n(a,`click`,()=>{t.muted=!1}),T)W(o,`display`,`none`);else{let e=!1;n(l,`mousedown`,n=>{e=n.button===0,t.volume=f(n)}),t.on(`document:mousemove`,n=>{e&&(t.muted=!1,t.volume=f(n))}),t.on(`document:mouseup`,()=>{e&&(e=!1)})}}})}function dn(e,t){let{constructor:n,template:{$player:r,$video:i}}=e;function a(t){q(t,r)?(e.isInput=t.target.tagName===`INPUT`,e.isFocus=!0,e.emit(`focus`,t)):(e.isInput=!1,e.isFocus=!1,e.emit(`blur`,t))}e.on(`document:click`,a),e.on(`document:contextmenu`,a);let o=[];t.proxy(i,`click`,t=>{let r=Date.now();o.push(r);let{MOBILE_CLICK_PLAY:i,DBCLICK_TIME:a,MOBILE_DBCLICK_PLAY:s,DBCLICK_FULLSCREEN:c}=n,l=o.filter(e=>r-e<=a);switch(l.length){case 1:e.emit(`click`,t),T?!e.isLock&&i&&e.toggle():e.toggle(),o=l;break;case 2:e.emit(`dblclick`,t),T?!e.isLock&&s&&e.toggle():c&&(e.fullscreen=!e.fullscreen),o=[];break;default:o=[]}})}function fn(e,t){return Math.atan2(t,e)*180/Math.PI}function pn(e,t,n,r){let i=t-r,a=n-e,o=0;if(Math.abs(a)<2&&Math.abs(i)<2)return o;let s=fn(a,i);return s>=-45&&s<45?o=4:s>=45&&s<135?o=1:s>=-135&&s<-45?o=2:(s>=135&&s<=180||s>=-180&&s<-135)&&(o=3),o}function mn(e,t){if(T&&!e.option.isLive){let{$video:n,$progress:r}=e.template,i=null,a=!1,o=0,s=0,c=0,l=t=>{if(t.touches.length===1&&!e.isLock){i===r&&an(e,t),a=!0;let{pageX:n,pageY:l}=t.touches[0];o=n,s=l,c=e.currentTime}},u=t=>{if(t.touches.length===1&&a&&e.duration){let{pageX:r,pageY:a}=t.touches[0],l=pn(o,s,r,a),u=[3,4].includes(l),d=[1,2].includes(l);if(u&&!e.isRotate||d&&e.isRotate){let l=Z((r-o)/e.width,-1,1),u=Z((a-s)/e.height,-1,1),d=e.isRotate?u:l,f=i===n?e.constructor.TOUCH_MOVE_RATIO:1,p=Z(c+e.duration*d*f,0,e.duration);e.seek=p,e.emit(`setBar`,`played`,Z(p/e.duration,0,1),t),e.notice.show=`${Q(p)} / ${Q(e.duration)}`}}};e.option.gesture&&(t.proxy(n,`touchstart`,e=>{i=n,l(e)}),t.proxy(n,`touchmove`,u)),t.proxy(r,`touchstart`,e=>{i=r,l(e)}),t.proxy(r,`touchmove`,u),e.on(`document:touchend`,()=>{a&&(o=0,s=0,c=0,a=!1,i=null)})}}function hn(e,t){let n=[`click`,`mouseup`,`keydown`,`touchend`,`touchmove`,`mousemove`,`pointerup`,`contextmenu`,`pointermove`,`visibilitychange`,`webkitfullscreenchange`],r=[`resize`,`scroll`,`orientationchange`],i=[];function a(a={}){for(let e=0;e<i.length;e++)t.remove(i[e]);i.length=0;let{$player:o}=e.template;n.forEach(n=>{let r=a.document||o.ownerDocument||document,s=t.proxy(r,n,t=>{e.emit(`document:${n}`,t)});i.push(s)}),r.forEach(n=>{let r=a.window||o.ownerDocument?.defaultView||window,s=t.proxy(r,n,t=>{e.emit(`window:${n}`,t)});i.push(s)})}a(),t.bindGlobalEvents=a}function gn(e,t){let{$player:n}=e.template;t.hover(n,t=>{B(n,`art-hover`),e.emit(`hover`,!0,t)},t=>{V(n,`art-hover`),e.emit(`hover`,!1,t)})}function _n(e,t){let{$player:n}=e.template;t.proxy(n,`mousemove`,t=>{e.emit(`mousemove`,t)})}function vn(e,t){let{option:n,constructor:r}=e;e.on(`resize`,()=>{let{aspectRatio:t,notice:r}=e;e.state===`standard`&&n.autoSize&&e.autoSize(),e.aspectRatio=t,r.show=``});let i=Ut(()=>e.emit(`resize`),r.RESIZE_TIME);e.on(`window:orientationchange`,()=>i()),e.on(`window:resize`,()=>i()),screen&&screen.orientation&&screen.orientation.onchange&&t.proxy(screen.orientation,`change`,()=>i())}function yn(e){if(e.constructor.USE_RAF){let t=null;(function n(){e.playing&&e.emit(`raf`),e.isDestroy||(t=requestAnimationFrame(n))})(),e.on(`destroy`,()=>{cancelAnimationFrame(t)})}}function bn(e){let{option:t,constructor:n,template:{$container:r}}=e,i=Wt(()=>{e.emit(`view`,wt(r,n.SCROLL_GAP))},n.SCROLL_TIME);e.on(`window:scroll`,()=>i()),e.on(`view`,n=>{t.autoMini&&(e.mini=!n)})}function xn(e){let{i18n:t,notice:n,proxy:r,template:{$video:i}}=e,a=!0;window.WebKitPlaybackTargetAvailabilityEvent&&i.webkitShowPlaybackTargetPicker?r(i,`webkitplaybacktargetavailabilitychanged`,e=>{switch(e.availability){case`available`:a=!0;break;case`not-available`:a=!1;break}}):a=!1,D(e,`airplay`,{value(){a?(i.webkitShowPlaybackTargetPicker(),e.emit(`airplay`)):n.show=t.get(`AirPlay Not Available`)}})}function Sn(e){let{i18n:t,notice:n,template:{$video:r,$player:i}}=e;D(e,`aspectRatio`,{get(){return i.dataset.aspectRatio||`default`},set(a){if(a||(a=`default`),a==="default")W(r,`width`,null),W(r,`height`,null),W(r,`margin`,null),delete i.dataset.aspectRatio;else{let e=a.split(`:`).map(Number),{clientWidth:t,clientHeight:n}=i,o=t/n,s=e[0]/e[1];o>s?(W(r,`width`,`${s*n}px`),W(r,`height`,`100%`),W(r,`margin`,`0 auto`)):(W(r,`width`,`100%`),W(r,`height`,`${t/s}px`),W(r,`margin`,`auto 0`)),i.dataset.aspectRatio=a}n.show=`${t.get(`Aspect Ratio`)}: ${a==="default"?t.get(`Default`):a}`,e.emit(`aspectRatio`,a)}})}function Cn(e){let{template:{$video:t}}=e;D(e,`attr`,{value(e,n){if(n===void 0)return t[e];t[e]=n}})}function wn(e){let{template:{$container:t,$video:n}}=e;D(e,`autoHeight`,{value(){let{clientWidth:r}=t,{videoHeight:i,videoWidth:a}=n,o=r/a*i;W(t,`height`,`${o}px`),e.emit(`autoHeight`,o)}})}function Tn(e){let{$container:t,$player:n,$video:r}=e.template;D(e,`autoSize`,{value(){let{videoWidth:i,videoHeight:a}=r,{width:o,height:s}=Y(t),c=i/a;if(o/s>c){let e=s*c/o*100;W(n,`width`,`${e}%`),W(n,`height`,`100%`)}else{let e=o/c/s*100;W(n,`width`,`100%`),W(n,`height`,`${e}%`)}e.emit(`autoSize`,{width:e.width,height:e.height})}})}function En(e){let{$player:t}=e.template;D(e,`cssVar`,{value(e,n){return n?t.style.setProperty(e,n):getComputedStyle(t).getPropertyValue(e)}})}function Dn(e){let{$video:t}=e.template;D(e,`currentTime`,{get:()=>t.currentTime||0,set:n=>{n=Number.parseFloat(n),!Number.isNaN(n)&&(t.currentTime=Z(n,0,e.duration))}})}function On(e){D(e,`duration`,{get:()=>{let{duration:t}=e.template.$video;return t===1/0?0:t||0}})}function kn(e){let{i18n:t,notice:n,option:r,constructor:i,proxy:a,template:{$player:o,$video:s,$poster:c}}=e,l=0;for(let t=0;t<x.events.length;t++)a(s,x.events[t],t=>{e.emit(`video:${t.type}`,t)});e.on(`video:canplay`,()=>{l=0,e.loading.show=!1}),e.once(`video:canplay`,()=>{e.loading.show=!1,e.controls.show=!0,e.mask.show=!0,e.isReady=!0,e.emit(`ready`)}),e.on(`video:ended`,()=>{r.loop?(e.seek=0,e.play(),e.controls.show=!1,e.mask.show=!1):(e.controls.show=!0,e.mask.show=!0)}),e.on(`video:error`,async a=>{l<i.RECONNECT_TIME_MAX?(await $(i.RECONNECT_SLEEP_TIME),l+=1,e.url=r.url,n.show=`${t.get(`Reconnect`)}: ${l}`,e.emit(`error`,a,l)):(e.mask.show=!0,e.loading.show=!1,e.controls.show=!0,B(o,`art-error`),await $(i.RECONNECT_SLEEP_TIME),n.show=t.get(`Video Load Failed`))}),e.on(`video:loadedmetadata`,()=>{e.emit(`resize`),T&&(e.loading.show=!1,e.controls.show=!0,e.mask.show=!0)}),e.on(`video:loadstart`,()=>{e.loading.show=!0,e.mask.show=!1,e.controls.show=!0}),e.on(`video:pause`,()=>{e.controls.show=!0,e.mask.show=!0}),e.on(`video:play`,()=>{e.mask.show=!1,W(c,`display`,`none`)}),e.on(`video:playing`,()=>{e.mask.show=!1}),e.on(`video:progress`,()=>{e.playing&&(e.loading.show=!1)}),e.on(`video:seeked`,()=>{e.loading.show=!1,e.mask.show=!0}),e.on(`video:seeking`,()=>{e.loading.show=!0,e.mask.show=!1}),e.on(`video:timeupdate`,()=>{e.mask.show=!1}),e.on(`video:waiting`,()=>{e.loading.show=!0,e.mask.show=!1})}function An(e){let{template:{$player:t},i18n:n,notice:r}=e;D(e,`flip`,{get(){return t.dataset.flip||`normal`},set(i){i||(i=`normal`),i===`normal`?delete t.dataset.flip:t.dataset.flip=i,r.show=`${n.get(`Video Flip`)}: ${n.get(Nt(i))}`,e.emit(`flip`,i)}})}function jn(e){let{i18n:t,notice:n,template:{$video:r,$player:i}}=e,a=e=>{I.on(`change`,()=>{e.emit(`fullscreen`,I.isFullscreen),I.isFullscreen?(e.state=`fullscreen`,B(i,`art-fullscreen`)):V(i,`art-fullscreen`),e.emit(`resize`)}),I.on(`error`,t=>{e.emit(`fullscreenError`,t)}),D(e,`fullscreen`,{get(){return I.isFullscreen},async set(e){e?await I.request(i):await I.exit()}})},o=e=>{e.on(`document:webkitfullscreenchange`,()=>{e.emit(`fullscreen`,e.fullscreen),e.emit(`resize`)}),D(e,`fullscreen`,{get(){return document.fullscreenElement===r},set(t){t?(e.state=`fullscreen`,r.webkitEnterFullscreen()):r.webkitExitFullscreen()}})};e.once(`video:loadedmetadata`,()=>{I.isEnabled?a(e):r.webkitSupportsFullscreen?o(e):D(e,`fullscreen`,{get(){return!1},set(){n.show=t.get(`Fullscreen Not Supported`)}}),D(e,`fullscreen`,Lt(e,`fullscreen`))})}function Mn(e){let{constructor:t,template:{$container:n,$player:r}}=e,i=``;D(e,`fullscreenWeb`,{get(){return H(r,`art-fullscreen-web`)},set(a){a?(i=r.style.cssText,t.FULLSCREEN_WEB_IN_BODY&&U(document.body,r),e.state=`fullscreenWeb`,W(r,`width`,`100%`),W(r,`height`,`100%`),B(r,`art-fullscreen-web`),e.emit(`fullscreenWeb`,!0)):(t.FULLSCREEN_WEB_IN_BODY&&U(n,r),i&&(r.style.cssText=i,i=``),V(r,`art-fullscreen-web`),e.emit(`fullscreenWeb`,!1)),e.emit(`resize`)}})}function Nn(e){let{$video:t}=e.template;D(e,`loaded`,{get:()=>e.loadedTime/t.duration}),D(e,`loadedTime`,{get:()=>t.buffered.length?t.buffered.end(t.buffered.length-1):0})}function Pn(e){let{icons:t,proxy:n,storage:r,template:{$player:i,$video:a}}=e,o=!1,s=0,c=0;function l(){let{$mini:t}=e.template;t&&(V(i,`art-mini`),W(t,`display`,`none`),i.prepend(a),e.emit(`mini`,!1))}function u(t,n){e.playing?(W(t,`display`,`none`),W(n,`display`,`flex`)):(W(t,`display`,`flex`),W(n,`display`,`none`))}function d(){let{$mini:i}=e.template;if(i)return U(i,a),W(i,`display`,`flex`);{let i=J(`div`);B(i,`art-mini-popup`),U(document.body,i),e.template.$mini=i,U(i,a);let d=U(i,`<div class="art-mini-close"></div>`);U(d,t.close),n(d,`click`,l);let f=U(i,`<div class="art-mini-state"></div>`),p=U(f,t.play),m=U(f,t.pause);return n(p,`click`,()=>e.play()),n(m,`click`,()=>e.pause()),u(p,m),e.on(`video:playing`,()=>u(p,m)),e.on(`video:pause`,()=>u(p,m)),e.on(`video:timeupdate`,()=>u(p,m)),n(i,`mousedown`,e=>{o=e.button===0,s=e.pageX,c=e.pageY}),e.on(`document:mousemove`,e=>{if(o){B(i,`art-mini-dragging`);let t=e.pageX-s,n=e.pageY-c;W(i,`transform`,`translate(${t}px, ${n}px)`)}}),e.on(`document:mouseup`,()=>{if(o){o=!1,V(i,`art-mini-dragging`);let e=Y(i);r.set(`left`,e.left),r.set(`top`,e.top),W(i,`left`,`${e.left}px`),W(i,`top`,`${e.top}px`),W(i,`transform`,null)}}),i}}function f(){let{$mini:t}=e.template,n=Y(t),i=window.innerHeight-n.height-50,a=window.innerWidth-n.width-50;r.set(`top`,i),r.set(`left`,a),W(t,`top`,`${i}px`),W(t,`left`,`${a}px`)}D(e,`mini`,{get(){return H(i,`art-mini`)},set(t){if(t){e.state=`mini`,B(i,`art-mini`);let t=d(),n=r.get(`top`),a=r.get(`left`);typeof n==`number`&&typeof a==`number`?(W(t,`top`,`${n}px`),W(t,`left`,`${a}px`),wt(t)||f()):f(),e.emit(`mini`,!0)}else l()}})}function Fn(e){let{option:t,storage:n,template:{$video:r,$poster:i}}=e;for(let n in t.moreVideoAttr)e.attr(n,t.moreVideoAttr[n]);t.muted&&(e.muted=t.muted),t.volume&&(r.volume=Z(t.volume,0,1));let a=n.get(`volume`);typeof a==`number`&&(r.volume=Z(a,0,1)),t.poster&&W(i,`backgroundImage`,`url(${t.poster})`),t.autoplay&&(r.autoplay=t.autoplay),t.playsInline&&(r.playsInline=!0,r[`webkit-playsinline`]=!0),t.theme&&(t.cssVar[`--art-theme`]=t.theme);for(let n in t.cssVar)e.cssVar(n,t.cssVar[n]);e.url=t.url}function In(e){let{template:{$video:t},i18n:n,notice:r}=e;D(e,`pause`,{value(){let i=t.pause();return r.show=n.get(`Pause`),e.emit(`pause`),i}})}function Ln(e){let{template:{$video:t},proxy:n,notice:r}=e;t.disablePictureInPicture=!1,D(e,`pip`,{get(){return document.pictureInPictureElement},set(n){n?(e.state=`pip`,t.requestPictureInPicture().catch(e=>{throw r.show=e,e})):document.exitPictureInPicture().catch(e=>{throw r.show=e,e})}}),n(t,`enterpictureinpicture`,()=>{e.emit(`pip`,!0)}),n(t,`leavepictureinpicture`,()=>{e.emit(`pip`,!1)})}function Rn(e){let{$video:t}=e.template;t.webkitSetPresentationMode(`inline`),D(e,`pip`,{get(){return t.webkitPresentationMode===`picture-in-picture`},set(n){n?(e.state=`pip`,t.webkitSetPresentationMode(`picture-in-picture`),e.emit(`pip`,!0)):(t.webkitSetPresentationMode(`inline`),e.emit(`pip`,!1))}})}function zn(e){let{i18n:t,notice:n,template:{$video:r}}=e;document.pictureInPictureEnabled?Ln(e):r.webkitSupportsPresentationMode?Rn(e):D(e,`pip`,{get(){return!1},set(){n.show=t.get(`PIP Not Supported`)}})}function Bn(e){let{template:{$video:t},i18n:n,notice:r}=e;D(e,`playbackRate`,{get(){return t.playbackRate},set(i){if(i){if(i===t.playbackRate)return;t.playbackRate=i,r.show=`${n.get(`Rate`)}: ${i===1?n.get(`Normal`):`${i}x`}`}else e.playbackRate=1}})}function Vn(e){D(e,`played`,{get:()=>e.currentTime/e.duration})}function Hn(e){let{$video:t}=e.template;D(e,`playing`,{get:()=>typeof t.playing==`boolean`?t.playing:t.currentTime>0&&!t.paused&&!t.ended&&t.readyState>2})}function Un(e){let{i18n:t,notice:n,option:r,constructor:{instances:i},template:{$video:a}}=e;D(e,`play`,{async value(){let o=await a.play();if(n.show=t.get(`Play`),e.emit(`play`),r.mutex)for(let t=0;t<i.length;t++){let n=i[t];n!==e&&n.pause()}return o}})}function Wn(e){let{template:{$poster:t}}=e;D(e,`poster`,{get:()=>{try{return t.style.backgroundImage.match(/"(.*)"/)[1]}catch{return``}},set(e){W(t,`backgroundImage`,`url(${e})`)}})}function Gn(e){D(e,`quality`,{set(t){let{controls:n,notice:r,i18n:i}=e,a=t.find(e=>e.default)||t[0];n.update({name:`quality`,position:`right`,index:10,style:{marginRight:`10px`},html:a?.html||``,selector:t,async onSelect(t){return await e.switchQuality(t.url),r.show=`${i.get(`Switch Video`)}: ${t.html}`,t.html}})}})}function Kn(e){D(e,`rect`,{get:()=>Y(e.template.$player)});let t=[`bottom`,`height`,`left`,`right`,`top`,`width`];for(let n=0;n<t.length;n++){let r=t[n];D(e,r,{get:()=>e.rect[r]})}D(e,`x`,{get:()=>e.left+window.pageXOffset}),D(e,`y`,{get:()=>e.top+window.pageYOffset})}function qn(e){let{notice:t,template:{$video:n}}=e,r=J(`canvas`);D(e,`getDataURL`,{value:()=>new Promise((e,i)=>{try{r.width=n.videoWidth,r.height=n.videoHeight,r.getContext(`2d`).drawImage(n,0,0),e(r.toDataURL(`image/png`))}catch(e){t.show=e,i(e)}})}),D(e,`getBlobUrl`,{value:()=>new Promise((e,i)=>{try{r.width=n.videoWidth,r.height=n.videoHeight,r.getContext(`2d`).drawImage(n,0,0),r.toBlob(t=>{e(URL.createObjectURL(t))})}catch(e){t.show=e,i(e)}})}),D(e,`screenshot`,{value:async t=>{let r=await e.getDataURL();return Mt(r,`${t||`artplayer_${Q(n.currentTime)}`}.png`),e.emit(`screenshot`,r),r}})}function Jn(e){let{notice:t}=e;D(e,`seek`,{set(n){e.currentTime=n,e.duration&&(t.show=`${Q(e.currentTime)} / ${Q(e.duration)}`),e.emit(`seek`,e.currentTime,n)}}),D(e,`forward`,{set(t){e.seek=e.currentTime+t}}),D(e,`backward`,{set(t){e.seek=e.currentTime-t}})}function Yn(e){let t=[`mini`,`pip`,`fullscreen`,`fullscreenWeb`];D(e,`state`,{get:()=>t.find(t=>e[t])||`standard`,set(n){for(let r=0;r<t.length;r++){let i=t[r];i!==n&&e[i]&&(e[i]=!1)}}})}function Xn(e){let{notice:t,i18n:n,template:r}=e;D(e,`subtitleOffset`,{get(){return r.$track?.offset||0},set(i){let{cues:a}=e.subtitle;if(!r.$track||a.length===0)return;let o=Z(i,-10,10);r.$track.offset=o;for(let t=0;t<a.length;t++){let n=a[t];n.originalStartTime=n.originalStartTime??n.startTime,n.originalEndTime=n.originalEndTime??n.endTime,n.startTime=Z(n.originalStartTime+o,0,e.duration),n.endTime=Z(n.originalEndTime+o,0,e.duration)}e.subtitle.update(),t.show=`${n.get(`Subtitle Offset`)}: ${i}s`,e.emit(`subtitleOffset`,i)}})}function Zn(e){function t(t,n){return new Promise((r,i)=>{if(t===e.url){r();return}let{playing:a,aspectRatio:o,playbackRate:s}=e;e.pause(),e.url=t,e.notice.show=``;let c={};c.error=t=>{e.off(`video:canplay`,c.canplay),e.off(`video:loadedmetadata`,c.metadata),i(t)},c.metadata=()=>{e.currentTime=n},c.canplay=async()=>{e.off(`video:error`,c.error),e.playbackRate=s,e.aspectRatio=o,a&&await e.play(),e.notice.show=``,r()},e.once(`video:error`,c.error),e.once(`video:loadedmetadata`,c.metadata),e.once(`video:canplay`,c.canplay)})}D(e,`switchQuality`,{value:n=>t(n,e.currentTime)}),D(e,`switchUrl`,{value:e=>t(e,0)}),D(e,`switch`,{set:e.switchUrl})}function Qn(e){D(e,`theme`,{get(){return e.cssVar(`--art-theme`)},set(t){e.cssVar(`--art-theme`,t)}})}function $n(e){let{option:t,template:{$progress:n,$video:r}}=e,i=null,a=!1,o=null;function s(){clearTimeout(i),i=null,a=!1,o=null}function c(i){let a=e.controls?.thumbnails;if(!a)return;let{number:s,column:c,width:l,height:u,scale:d}=t.thumbnails,f=l*d||o.naturalWidth/c,p=u*d||f/(r.videoWidth/r.videoHeight),m=n.clientWidth/s,h=Math.floor(i/m),g=Math.ceil(h/c)-1,_=h%c||c-1;W(a,`backgroundImage`,`url(${o.src})`),W(a,`height`,`${p}px`),W(a,`width`,`${f}px`),W(a,`backgroundPosition`,`-${_*f}px -${g*p}px`),i<=f/2?W(a,`left`,0):i>n.clientWidth-f/2?W(a,`left`,`${n.clientWidth-f}px`):W(a,`left`,`${i-f/2}px`)}e.on(`setBar`,async(r,i,s)=>{let l=e.controls?.thumbnails,{url:u,scale:d}=t.thumbnails;if(!(!l||!u)&&(r===`hover`||r===`played`&&s&&T)){if(!o&&!a&&(a=!0,o=await kt(u,d),a=!1),!o)return;let e=n.clientWidth*i;e>0&&e<n.clientWidth&&c(e)}}),D(e,`thumbnails`,{get(){return e.option.thumbnails},set(t){t.url&&!e.option.isLive&&(e.option.thumbnails=t,s())}})}function er(e){D(e,`toggle`,{value(){return e.playing?e.pause():e.play()}})}function tr(e){D(e,`type`,{get(){return e.option.type},set(t){e.option.type=t}})}function nr(e){let{option:t,template:{$video:n}}=e;D(e,`url`,{get(){return n.src},async set(r){if(r){let i=e.url,a=t.type||jt(r),o=t.customType[a];a&&o?(await $(),e.loading.show=!0,o.call(e,n,r,e)):(URL.revokeObjectURL(i),n.src=r),i!==e.url&&(e.option.url=r,e.isReady&&i&&e.once(`video:canplay`,()=>{e.emit(`restart`,r)}))}else await $(),e.loading.show=!0}})}function rr(e){let{template:{$video:t},i18n:n,notice:r,storage:i}=e;D(e,`volume`,{get:()=>t.volume||0,set:e=>{t.volume=Z(e,0,1),r.show=`${n.get(`Volume`)}: ${Number.parseInt(t.volume*100,10)}`,t.volume!==0&&i.set(`volume`,t.volume)}}),D(e,`muted`,{get:()=>t.muted,set:n=>{t.muted=n,e.emit(`muted`,n)}})}function ir(e){let{notice:t,constructor:n,template:{$player:r,$video:i}}=e,a=`art-auto-orientation`,o=`art-auto-orientation-fullscreen`,s=!1;function c(){let t=document.documentElement.clientWidth,n=document.documentElement.clientHeight;W(r,`width`,`${n}px`),W(r,`height`,`${t}px`),W(r,`transform-origin`,`0 0`),W(r,`transform`,`rotate(90deg) translate(0, -${t}px)`),B(r,a),e.isRotate=!0,e.emit(`resize`)}function l(){W(r,`width`,``),W(r,`height`,``),W(r,`transform-origin`,``),W(r,`transform`,``),V(r,a),e.isRotate=!1,e.emit(`resize`)}function u(){let{videoWidth:e,videoHeight:t}=i,n=document.documentElement.clientWidth,r=document.documentElement.clientHeight;return e>t&&n<r||e<t&&n>r}return e.on(`fullscreenWeb`,t=>{if(t){if(u()){let t=Number(n.AUTO_ORIENTATION_TIME??0);setTimeout(()=>{e.fullscreenWeb&&!H(r,a)&&c()},t)}}else H(r,a)&&l()}),e.on(`fullscreen`,async e=>{let n=!!screen?.orientation?.lock;if(e){if(n&&u())try{let e=screen.orientation.type.startsWith(`portrait`)?`landscape`:`portrait`;await screen.orientation.lock(e),s=!0,B(r,o)}catch(e){s=!1,t.show=e}}else if(H(r,o)&&V(r,o),n&&s){try{screen.orientation.unlock()}catch{}s=!1}}),{name:`autoOrientation`,get state(){return H(r,a)}}}function ar(e){let{i18n:t,icons:n,storage:r,constructor:i,proxy:a,template:{$poster:o}}=e,s=e.layers.add({name:`auto-playback`,html:`
            <div class="art-auto-playback-close"></div>
            <div class="art-auto-playback-last"></div>
            <div class="art-auto-playback-jump"></div>
        `}),c=z(`.art-auto-playback-last`,s),l=z(`.art-auto-playback-jump`,s),u=z(`.art-auto-playback-close`,s);U(u,n.close);let d=null;e.on(`video:timeupdate`,()=>{if(e.playing){let t=r.get(`times`)||{},n=Object.keys(t);n.length>i.AUTO_PLAYBACK_MAX&&delete t[n[0]],t[e.option.id||e.option.url]=e.currentTime,r.set(`times`,t)}});function f(){let n=(r.get(`times`)||{})[e.option.id||e.option.url];clearTimeout(d),W(s,`display`,`none`),n&&n>=i.AUTO_PLAYBACK_MIN&&(W(s,`display`,`flex`),c.textContent=`${t.get(`Last Seen`)} ${Q(n)}`,l.textContent=t.get(`Jump Play`),a(u,`click`,()=>{W(s,`display`,`none`)}),a(l,`click`,()=>{e.seek=n,e.play(),W(o,`display`,`none`),W(s,`display`,`none`)}),e.once(`video:timeupdate`,()=>{d=setTimeout(()=>{W(s,`display`,`none`)},i.AUTO_PLAYBACK_TIMEOUT)}))}return e.on(`ready`,f),e.on(`restart`,f),{name:`auto-playback`,get times(){return r.get(`times`)||{}},clear(){return r.del(`times`)},delete(e){let t=r.get(`times`)||{};return delete t[e],r.set(`times`,t),t}}}function or(e){let{constructor:t,proxy:n,template:{$player:r,$video:i}}=e,a=null,o=!1,s=1,c=n=>{n.touches.length===1&&e.playing&&!e.isLock&&(a=setTimeout(()=>{o=!0,s=e.playbackRate,e.playbackRate=t.FAST_FORWARD_VALUE,B(r,`art-fast-forward`)},t.FAST_FORWARD_TIME))},l=()=>{clearTimeout(a),o&&(o=!1,e.playbackRate=s,V(r,`art-fast-forward`))};return n(i,`touchstart`,c),e.on(`document:touchmove`,l),e.on(`document:touchend`,l),{name:`fastForward`,get state(){return H(r,`art-fast-forward`)}}}function sr(e){let{layers:t,icons:n,template:{$player:r}}=e;function i(){return H(r,`art-lock`)}function a(){B(r,`art-lock`),e.isLock=!0,e.emit(`lock`,!0)}function o(){V(r,`art-lock`),e.isLock=!1,e.emit(`lock`,!1)}return t.add({name:`lock`,mounted(t){let r=U(t,n.lock),i=U(t,n.unlock);W(r,`display`,`none`),e.on(`lock`,e=>{e?(W(r,`display`,`inline-flex`),W(i,`display`,`none`)):(W(r,`display`,`none`),W(i,`display`,`inline-flex`))})},click(){i()?o():a()}}),{name:`lock`,get state(){return i()},set state(e){e?a():o()}}}function cr(e){return e.on(`control`,t=>{t?V(e.template.$player,`art-mini-progress-bar`):B(e.template.$player,`art-mini-progress-bar`)}),{name:`mini-progress-bar`}}function lr(e){let{i18n:t,icons:n,constructor:{SETTING_ITEM_WIDTH:r,ASPECT_RATIO:i}}=e;function a(e){return e==="default"?t.get(`Default`):e}function o(){let t=e.setting.find(`aspect-ratio-${e.aspectRatio}`);e.setting.check(t)}return{width:r,name:`aspect-ratio`,html:t.get(`Aspect Ratio`),icon:n.aspectRatio,tooltip:a(e.aspectRatio),selector:i.map(t=>({value:t,name:`aspect-ratio-${t}`,default:t===e.aspectRatio,html:a(t)})),onSelect(t){return e.aspectRatio=t.value,t.html},mounted:()=>{o(),e.on(`aspectRatio`,()=>o())}}}function ur(e){let{i18n:t,icons:n,constructor:{SETTING_ITEM_WIDTH:r,FLIP:i}}=e;function a(e){return t.get(Nt(e))}function o(){let t=e.setting.find(`flip-${e.flip}`);e.setting.check(t)}return{width:r,name:`flip`,html:t.get(`Video Flip`),tooltip:a(e.flip),icon:n.flip,selector:i.map(t=>({value:t,name:`flip-${t}`,default:t===e.flip,html:a(t)})),onSelect(t){return e.flip=t.value,t.html},mounted:()=>{o(),e.on(`flip`,()=>o())}}}function dr(e){let{i18n:t,icons:n,constructor:{SETTING_ITEM_WIDTH:r,PLAYBACK_RATE:i}}=e;function a(e){return e===1?t.get(`Normal`):e.toFixed(1)}function o(){let t=e.setting.find(`playback-rate-${e.playbackRate}`);e.setting.check(t)}return{width:r,name:`playback-rate`,html:t.get(`Play Speed`),tooltip:a(e.playbackRate),icon:n.playbackRate,selector:i.map(t=>({value:t,name:`playback-rate-${t}`,default:t===e.playbackRate,html:a(t)})),onSelect(t){return e.playbackRate=t.value,t.html},mounted:()=>{o(),e.on(`video:ratechange`,()=>o())}}}function fr(e){let{i18n:t,icons:n,constructor:r}=e;return{width:r.SETTING_ITEM_WIDTH,name:`subtitle-offset`,html:t.get(`Subtitle Offset`),icon:n.subtitle,tooltip:`0s`,range:[0,-10,10,.1],onChange(t){return e.subtitleOffset=t.range[0],`${t.range[0]}s`},mounted:(t,n)=>{e.on(`subtitleOffset`,e=>{n.$range.value=e,n.tooltip=`${e}s`})}}}return{setters:[function(e){n=e.Br,r=e.Gn,i=e.J,a=e.Jn,o=e.Jr,s=e.K,c=e.Ki,l=e.Mi,u=e.Q,d=e.T,f=e.Ui,p=e.Ut,m=e.Xn,h=e.Xr,g=e.Yn,_=e.Zi,ee=e.aa,te=e.bn,ne=e.f,re=e.ft,ie=e.nt,ae=e.q,oe=e.sn,se=e.ta,ce=e.zn},function(e){le=e.a,ue=e.u},function(e){de=e.t}],execute:function(){v={exports:{}},fe=v.exports,y=_t(vt()),b=`5.4.0`,x={properties:`audioTracks.autoplay.buffered.controller.controls.crossOrigin.currentSrc.currentTime.defaultMuted.defaultPlaybackRate.duration.ended.error.loop.mediaGroup.muted.networkState.paused.playbackRate.played.preload.readyState.seekable.seeking.src.startDate.textTracks.videoTracks.volume`.split(`.`),methods:[`addTextTrack`,`canPlayType`,`load`,`play`,`pause`],events:[`abort`,`canplay`,`canplaythrough`,`durationchange`,`emptied`,`ended`,`error`,`loadeddata`,`loadedmetadata`,`loadstart`,`pause`,`play`,`playing`,`progress`,`ratechange`,`seeked`,`seeking`,`stalled`,`suspend`,`timeupdate`,`volumechange`,`waiting`],prototypes:[`width`,`height`,`videoWidth`,`videoHeight`,`poster`,`webkitDecodedFrameCount`,`webkitDroppedFrameCount`,`playsInline`,`webkitSupportsFullscreen`,`webkitDisplayingFullscreen`,`onenterpictureinpicture`,`onleavepictureinpicture`,`disablePictureInPicture`,`cancelVideoFrameCallback`,`requestVideoFrameCallback`,`getVideoPlaybackQuality`,`requestPictureInPicture`,`webkitEnterFullScreen`,`webkitEnterFullscreen`,`webkitExitFullScreen`,`webkitExitFullscreen`]},S=globalThis?.CUSTOM_USER_AGENT??(typeof navigator<`u`?navigator.userAgent:``),C=/^(?:(?!chrome|android).)*safari/i.test(S),w=/iPad|iPhone|iPod/i.test(S)&&!window.MSStream,me=w||S.includes(`Macintosh`)&&navigator.maxTouchPoints>=1,T=/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(S)||me,E=typeof window<`u`&&typeof document<`u`,he=class extends Error{constructor(e,t){super(e),typeof Error.captureStackTrace==`function`&&Error.captureStackTrace(this,t||this.constructor),this.name=`ArtPlayerError`}},D=Object.defineProperty,{hasOwnProperty:ge}=Object.prototype,_e=Object.freeze(Object.defineProperty({__proto__:null,ArtPlayerError:he,addClass:B,append:U,assToVtt:Ht,capitalize:Nt,clamp:Z,createElement:J,debounce:Ut,def:D,download:Mt,errorHandle:X,escape:Pt,get:Lt,getComposedPath:At,getExt:jt,getIcon:Et,getRect:Y,getStyle:St,has:It,hasClass:H,includeFromEvent:q,inverseClass:G,isBrowser:E,isIOS:w,isIOS13:me,isInViewport:wt,isMobile:T,isSafari:C,loadImg:kt,mergeDeep:Rt,query:z,queryAll:yt,remove:bt,removeClass:V,replaceElement:Tt,secondToTime:Q,setStyle:W,setStyleText:Dt,setStyles:xt,siblings:Ct,sleep:$,srtToVtt:Bt,supportsFlex:Ot,throttle:Wt,tooltip:K,unescape:Ft,userAgent:S,vttToBlob:Vt},Symbol.toStringTag,{value:`Module`})),ve=`array`,O=`boolean`,k=`string`,A=`number`,j=`object`,M=`function`,N={html:Gt,disable:`?${O}`,name:`?${k}`,index:`?${A}`,style:`?${j}`,click:`?${M}`,mounted:`?${M}`,tooltip:`?${k}|${A}`,width:`?${A}`,selector:`?${ve}`,onSelect:`?${M}`,switch:`?${O}`,onSwitch:`?${M}`,range:`?${ve}`,onRange:`?${M}`,onChange:`?${M}`},ye={id:k,container:Gt,url:k,poster:k,type:k,theme:k,lang:k,volume:A,isLive:O,muted:O,autoplay:O,autoSize:O,autoMini:O,loop:O,flip:O,playbackRate:O,aspectRatio:O,screenshot:O,setting:O,hotkey:O,pip:O,mutex:O,backdrop:O,fullscreen:O,fullscreenWeb:O,subtitleOffset:O,miniProgressBar:O,useSSR:O,playsInline:O,lock:O,gesture:O,fastForward:O,autoPlayback:O,autoOrientation:O,airplay:O,proxy:`?${M}`,plugins:[M],layers:[N],contextmenu:[N],settings:[N],controls:[{...N,position:(e,t,n)=>{let r=[`top`,`left`,`right`];return X(r.includes(e),`${n.join(`.`)} only accept ${r.toString()} as parameters`)}}],quality:[{default:`?${O}`,html:k,url:k}],highlight:[{time:A,text:k}],thumbnails:{url:k,number:A,column:A,width:A,height:A,scale:A},subtitle:{url:k,name:k,type:k,style:j,escape:O,encoding:k,onVttLoad:M},moreVideoAttr:j,i18n:j,icons:j,cssVar:j,customType:j},P=class{constructor(e){this.id=0,this.art=e,this.cache=new Map,this.add=this.add.bind(this),this.remove=this.remove.bind(this),this.update=this.update.bind(this)}get show(){return H(this.art.template.$player,`art-${this.name}-show`)}set show(e){let{$player:t}=this.art.template,n=`art-${this.name}-show`;e?B(t,n):V(t,n),this.art.emit(this.name,e)}toggle(){this.show=!this.show}add(e){let t=typeof e==`function`?e(this.art):e;if(t.html=t.html||``,y(t,N),!this.$parent||!this.name||t.disable)return;let n=t.name||`${this.name}${this.id}`;X(!this.cache.has(n),`Can't add an existing [${n}] to the [${this.name}]`),this.id+=1;let r=J(`div`);B(r,`art-${this.name}`),B(r,`art-${this.name}-${n}`);let i=Array.from(this.$parent.children);r.dataset.index=t.index||this.id;let a=i.find(e=>Number(e.dataset.index)>=Number(r.dataset.index));a?a.insertAdjacentElement(`beforebegin`,r):U(this.$parent,r),t.html&&U(r,t.html),t.style&&xt(r,t.style),t.tooltip&&K(r,t.tooltip);let o=[];if(t.click){let e=this.art.events.proxy(r,`click`,e=>{e.preventDefault(),t.click.call(this.art,this,e)});o.push(e)}return t.selector&&[`left`,`right`].includes(t.position)&&this.selector(t,r,o),this[n]=r,this.cache.set(n,{$ref:r,events:o,option:t}),t.mounted&&t.mounted.call(this.art,r),r}remove(e){X(this.cache.has(e),`Can't find [${e}] from the [${this.name}]`);let t=this.cache.get(e);t.option.beforeUnmount&&t.option.beforeUnmount.call(this.art,t.$ref);for(let e of t.events)this.art.events.remove(e);this.cache.delete(e),delete this[e],bt(t.$ref)}update(e){if(this.cache.has(e.name)){let t=this.cache.get(e.name);e=Object.assign(t.option,e),this.remove(e.name)}return this.add(e)}},be=class extends P{constructor(e){super(e),this.name=`contextmenu`,this.$parent=e.template.$contextmenu,T||this.init()}init(){let{option:e,proxy:t,template:{$player:n,$contextmenu:r}}=this.art;e.playbackRate&&this.add(Xt({name:`playbackRate`,index:10})),e.aspectRatio&&this.add(Kt({name:`aspectRatio`,index:20})),e.flip&&this.add(Jt({name:`flip`,index:30})),this.add(Yt({name:`info`,index:40})),this.add(Zt({name:`version`,index:50})),this.add(qt({name:`close`,index:60}));for(let t=0;t<e.contextmenu.length;t++)this.add(e.contextmenu[t]);t(n,`contextmenu`,e=>{if(!this.art.constructor.CONTEXTMENU)return;e.preventDefault(),this.show=!0;let t=e.clientX,i=e.clientY,{height:a,width:o,left:s,top:c}=Y(n),{height:l,width:u}=Y(r),d=t-s,f=i-c;t+u>s+o&&(d=o-u),i+l>c+a&&(f=a-l),xt(r,{top:`${f}px`,left:`${d}px`})}),t(n,`click`,e=>{q(e,r)||(this.show=!1)}),this.art.on(`blur`,()=>{this.show=!1})}},xe=class extends P{constructor(e){super(e),this.isHover=!1,this.name=`control`,this.timer=Date.now();let{constructor:t}=e,{$player:n,$bottom:r}=this.art.template;e.on(`mousemove`,()=>{T||(this.show=!0)}),e.on(`click`,()=>{T?this.toggle():this.show=!0}),e.on(`document:mousemove`,e=>{this.isHover=q(e,r)}),e.on(`video:timeupdate`,()=>{!e.setting.show&&!this.isHover&&!e.isInput&&e.playing&&this.show&&Date.now()-this.timer>=t.CONTROL_HIDE_TIME&&(this.show=!1)}),e.on(`control`,e=>{e?(V(n,`art-hide-cursor`),B(n,`art-hover`),this.timer=Date.now()):(B(n,`art-hide-cursor`),V(n,`art-hover`))}),this.init()}init(){let{option:e}=this.art;e.isLive||this.add(on({name:`progress`,position:`top`,index:10})),this.add({name:`thumbnails`,position:`top`,index:20}),this.add(nn({name:`playAndPause`,position:`left`,index:10})),this.add(un({name:`volume`,position:`left`,index:20})),e.isLive||this.add(ln({name:`time`,position:`left`,index:30})),e.quality.length&&$().then(()=>{this.art.quality=e.quality}),e.screenshot&&!T&&this.add(sn({name:`screenshot`,position:`right`,index:20})),e.setting&&this.add(cn({name:`setting`,position:`right`,index:30})),e.pip&&this.add(tn({name:`pip`,position:`right`,index:40})),e.airplay&&window.WebKitPlaybackTargetAvailabilityEvent&&this.add(Qt({name:`airplay`,position:`right`,index:50})),e.fullscreenWeb&&this.add(en({name:`fullscreenWeb`,position:`right`,index:60})),e.fullscreen&&this.add($t({name:`fullscreen`,position:`right`,index:70}));for(let t=0;t<e.controls.length;t++)this.add(e.controls[t])}add(e){let t=typeof e==`function`?e(this.art):e,{$progress:n,$controlsLeft:r,$controlsRight:i}=this.art.template;switch(t.position){case`top`:this.$parent=n;break;case`left`:this.$parent=r;break;case`right`:this.$parent=i;break;default:X(!1,`Control option.position must one of 'top', 'left', 'right'`);break}super.add(t)}check(e){if(e){e.$control_value.innerHTML=e.html;for(let t=0;t<e.$control_option.length;t++){let n=e.$control_option[t];n.default=n===e,n.default&&G(n.$control_item,`art-current`)}}}selector(e,t,n){let{proxy:r}=this.art.events;B(t,`art-control-selector`);let i=J(`div`);B(i,`art-selector-value`),U(i,e.html),t.textContent=``,U(t,i);let a=J(`div`);B(a,`art-selector-list`),U(t,a);for(let t=0;t<e.selector.length;t++){let n=e.selector[t],r=J(`div`);B(r,`art-selector-item`),n.default&&B(r,`art-current`),r.dataset.index=t,r.dataset.value=n.value,r.innerHTML=n.html,U(a,r),D(n,`$control_option`,{get:()=>e.selector}),D(n,`$control_item`,{get:()=>r}),D(n,`$control_value`,{get:()=>i})}let o=r(a,`click`,async t=>{let n=At(t),r=e.selector.find(e=>e.$control_item===n.find(t=>e.$control_item===t));this.check(r),e.onSelect&&(i.innerHTML=await e.onSelect.call(this.art,r,r.$control_item,t))});n.push(o)}},Se=class{constructor(e){this.destroyEvents=new Set,this.proxy=this.proxy.bind(this),this.hover=this.hover.bind(this),dn(e,this),gn(e,this),_n(e,this),vn(e,this),mn(e,this),bn(e),hn(e,this),yn(e)}proxy(e,t,n,r={}){if(Array.isArray(t))return t.map(t=>this.proxy(e,t,n,r));e.addEventListener(t,n,r);let i=()=>e.removeEventListener(t,n,r);return this.destroyEvents.add(i),i}hover(e,t,n){t&&this.proxy(e,`mouseenter`,t),n&&this.proxy(e,`mouseleave`,n)}remove(e){if(this.destroyEvents.has(e))try{e()}catch(e){console.warn(`Failed to remove event listener:`,e)}finally{this.destroyEvents.delete(e)}}destroy(){for(let e of this.destroyEvents)try{e()}catch(e){console.warn(`Failed to destroy event listener:`,e)}this.destroyEvents.clear()}},Ce=class{constructor(e){this.art=e,this.keys={},T||this.init()}init(){let{constructor:e}=this.art;this.art.option.hotkey&&(this.add(`Escape`,()=>{this.art.fullscreenWeb&&(this.art.fullscreenWeb=!1)}),this.add(`Space`,()=>{this.art.toggle()}),this.add(`ArrowLeft`,()=>{this.art.backward=e.SEEK_STEP}),this.add(`ArrowUp`,()=>{this.art.volume+=e.VOLUME_STEP}),this.add(`ArrowRight`,()=>{this.art.forward=e.SEEK_STEP}),this.add(`ArrowDown`,()=>{this.art.volume-=e.VOLUME_STEP})),this.art.on(`document:keydown`,e=>{if(this.art.isFocus){let t=document.activeElement.tagName.toUpperCase(),n=document.activeElement.getAttribute(`contenteditable`);if(t!==`INPUT`&&t!==`TEXTAREA`&&n!==``&&n!==`true`&&!e.altKey&&!e.ctrlKey&&!e.metaKey&&!e.shiftKey){let t=this.keys[e.code];if(t){e.preventDefault();for(let n=0;n<t.length;n++)t[n].call(this.art,e);this.art.emit(`hotkey`,e)}}}this.art.emit(`keydown`,e)})}add(e,t){return this.keys[e]?this.keys[e].includes(t)||this.keys[e].push(t):this.keys[e]=[t],this}remove(e,t){if(this.keys[e]){let n=this.keys[e].indexOf(t);n!==-1&&this.keys[e].splice(n,1),this.keys[e].length===0&&delete this.keys[e]}return this}},we={"Video Info":`统计信息`,Close:`关闭`,"Video Load Failed":`加载失败`,Volume:`音量`,Play:`播放`,Pause:`暂停`,Rate:`速度`,Mute:`静音`,"Video Flip":`画面翻转`,Horizontal:`水平`,Vertical:`垂直`,Reconnect:`重新连接`,"Show Setting":`显示设置`,"Hide Setting":`隐藏设置`,Screenshot:`截图`,"Play Speed":`播放速度`,"Aspect Ratio":`画面比例`,Default:`默认`,Normal:`正常`,Open:`打开`,"Switch Video":`切换`,"Switch Subtitle":`切换字幕`,Fullscreen:`全屏`,"Exit Fullscreen":`退出全屏`,"Web Fullscreen":`网页全屏`,"Exit Web Fullscreen":`退出网页全屏`,"Mini Player":`迷你播放器`,"PIP Mode":`开启画中画`,"Exit PIP Mode":`退出画中画`,"PIP Not Supported":`不支持画中画`,"Fullscreen Not Supported":`不支持全屏`,"Subtitle Offset":`字幕偏移`,"Last Seen":`上次看到`,"Jump Play":`跳转播放`,AirPlay:`隔空播放`,"AirPlay Not Available":`隔空播放不可用`},typeof window<`u`&&(window[`artplayer-i18n-zh-cn`]=we),Te=class{constructor(e){this.art=e,this.languages={"zh-cn":we},this.language={},this.update(e.option.i18n)}init(){let e=this.art.option.lang.toLowerCase();this.language=this.languages[e]||{}}get(e){return this.language[e]||e}update(e){this.languages=Rt(this.languages,e),this.init()}},Ee=`<svg width="18px" height="18px" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
    <g>
        <path d="M16,1 L2,1 C1.447,1 1,1.447 1,2 L1,12 C1,12.553 1.447,13 2,13 L5,13 L5,11 L3,11 L3,3 L15,3 L15,11 L13,11 L13,13 L16,13 C16.553,13 17,12.553 17,12 L17,2 C17,1.447 16.553,1 16,1 L16,1 Z"></path>
        <polygon points="4 17 14 17 9 11"></polygon>
    </g>
</svg>
`,De=`<svg xmlns="http://www.w3.org/2000/svg" height="32" width="32" version="1.1" viewBox="0 0 32 32">
    <path d="M 19.41,20.09 14.83,15.5 19.41,10.91 18,9.5 l -6,6 6,6 z" />
</svg>`,Oe=`<svg xmlns="http://www.w3.org/2000/svg" height="32" width="32" version="1.1" viewBox="0 0 32 32">
    <path d="m 12.59,20.34 4.58,-4.59 -4.58,-4.59 1.41,-1.41 6,6 -6,6 z" />
</svg>`,ke=`<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 88 88" preserveAspectRatio="xMidYMid meet" style="width: 100%; height: 100%; transform: translate3d(0px, 0px, 0px);"><defs><clipPath id="__lottie_element_216"><rect width="88" height="88" x="0" y="0"></rect></clipPath></defs><g clip-path="url(#__lottie_element_216)"><g transform="matrix(1,0,0,1,44,44)" opacity="1" style="display: block;"><g opacity="1" transform="matrix(1,0,0,1,0,0)"><path fill-opacity="1" d=" M12.437999725341797,-12.70199966430664 C12.437999725341797,-12.70199966430664 9.618000030517578,-9.881999969482422 9.618000030517578,-9.881999969482422 C8.82800006866455,-9.092000007629395 8.82800006866455,-7.831999778747559 9.618000030517578,-7.052000045776367 C9.618000030517578,-7.052000045776367 16.687999725341797,0.017999999225139618 16.687999725341797,0.017999999225139618 C16.687999725341797,0.017999999225139618 9.618000030517578,7.0879998207092285 9.618000030517578,7.0879998207092285 C8.82800006866455,7.877999782562256 8.82800006866455,9.137999534606934 9.618000030517578,9.918000221252441 C9.618000030517578,9.918000221252441 12.437999725341797,12.748000144958496 12.437999725341797,12.748000144958496 C13.227999687194824,13.527999877929688 14.48799991607666,13.527999877929688 15.267999649047852,12.748000144958496 C15.267999649047852,12.748000144958496 26.58799934387207,1.437999963760376 26.58799934387207,1.437999963760376 C27.368000030517578,0.6579999923706055 27.368000030517578,-0.6119999885559082 26.58799934387207,-1.3919999599456787 C26.58799934387207,-1.3919999599456787 15.267999649047852,-12.70199966430664 15.267999649047852,-12.70199966430664 C14.48799991607666,-13.491999626159668 13.227999687194824,-13.491999626159668 12.437999725341797,-12.70199966430664z M-12.442000389099121,-12.70199966430664 C-13.182000160217285,-13.442000389099121 -14.362000465393066,-13.482000350952148 -15.142000198364258,-12.821999549865723 C-15.142000198364258,-12.821999549865723 -15.272000312805176,-12.70199966430664 -15.272000312805176,-12.70199966430664 C-15.272000312805176,-12.70199966430664 -26.582000732421875,-1.3919999599456787 -26.582000732421875,-1.3919999599456787 C-27.32200050354004,-0.6520000100135803 -27.36199951171875,0.5180000066757202 -26.70199966430664,1.3079999685287476 C-26.70199966430664,1.3079999685287476 -26.582000732421875,1.437999963760376 -26.582000732421875,1.437999963760376 C-26.582000732421875,1.437999963760376 -15.272000312805176,12.748000144958496 -15.272000312805176,12.748000144958496 C-14.531999588012695,13.48799991607666 -13.362000465393066,13.527999877929688 -12.571999549865723,12.868000030517578 C-12.571999549865723,12.868000030517578 -12.442000389099121,12.748000144958496 -12.442000389099121,12.748000144958496 C-12.442000389099121,12.748000144958496 -9.612000465393066,9.918000221252441 -9.612000465393066,9.918000221252441 C-8.871999740600586,9.178000450134277 -8.831999778747559,8.008000373840332 -9.501999855041504,7.2179999351501465 C-9.501999855041504,7.2179999351501465 -9.612000465393066,7.0879998207092285 -9.612000465393066,7.0879998207092285 C-9.612000465393066,7.0879998207092285 -16.68199920654297,0.017999999225139618 -16.68199920654297,0.017999999225139618 C-16.68199920654297,0.017999999225139618 -9.612000465393066,-7.052000045776367 -9.612000465393066,-7.052000045776367 C-8.871999740600586,-7.791999816894531 -8.831999778747559,-8.961999893188477 -9.501999855041504,-9.751999855041504 C-9.501999855041504,-9.751999855041504 -9.612000465393066,-9.881999969482422 -9.612000465393066,-9.881999969482422 C-9.612000465393066,-9.881999969482422 -12.442000389099121,-12.70199966430664 -12.442000389099121,-12.70199966430664z M28,-28 C32.41999816894531,-28 36,-24.420000076293945 36,-20 C36,-20 36,20 36,20 C36,24.420000076293945 32.41999816894531,28 28,28 C28,28 -28,28 -28,28 C-32.41999816894531,28 -36,24.420000076293945 -36,20 C-36,20 -36,-20 -36,-20 C-36,-24.420000076293945 -32.41999816894531,-28 -28,-28 C-28,-28 28,-28 28,-28z" data-darkreader-inline-fill="" style="--darkreader-inline-fill:#a8a6a4;"></path></g></g></g></svg>`,Ae=`<svg xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="0 0 24 24" style="width: 100%; height: 100%;">
<path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z" />
</svg>`,je=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg t="1655876154826" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="22" height="22">
<path d="M571.733333 512l268.8-268.8c17.066667-17.066667 17.066667-42.666667 0-59.733333-17.066667-17.066667-42.666667-17.066667-59.733333 0L512 452.266667 243.2 183.466667c-17.066667-17.066667-42.666667-17.066667-59.733333 0-17.066667 17.066667-17.066667 42.666667 0 59.733333L452.266667 512 183.466667 780.8c-17.066667 17.066667-17.066667 42.666667 0 59.733333 8.533333 8.533333 19.2 12.8 29.866666 12.8s21.333333-4.266667 29.866667-12.8L512 571.733333l268.8 268.8c8.533333 8.533333 19.2 12.8 29.866667 12.8s21.333333-4.266667 29.866666-12.8c17.066667-17.066667 17.066667-42.666667 0-59.733333L571.733333 512z" p-id="2131">
</path>
</svg>`,Me=`<svg height="24" viewBox="0 0 24 24" width="24"><path d="M15,17h6v1h-6V17z M11,17H3v1h8v2h1v-2v-1v-2h-1V17z M14,8h1V6V5V3h-1v2H3v1h11V8z            M18,5v1h3V5H18z M6,14h1v-2v-1V9H6v2H3v1 h3V14z M10,12h11v-1H10V12z" data-darkreader-inline-fill="" style="--darkreader-inline-fill:#a8a6a4;"></path></svg>`,Ne=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg t="1652850026663" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2749" xmlns:xlink="http://www.w3.org/1999/xlink" width="50" height="50">
<path d="M593.8176 168.5504l356.00384 595.21024c26.15296 43.74528 10.73152 99.7376-34.44736 125.05088-14.39744 8.06912-30.72 12.30848-47.37024 12.30848H155.97568C103.75168 901.12 61.44 860.16 61.44 809.61536c0-16.09728 4.38272-31.92832 12.71808-45.8752L430.16192 168.5504c26.17344-43.7248 84.00896-58.65472 129.20832-33.34144a93.0816 93.0816 0 0 1 34.44736 33.34144zM512 819.2a61.44 61.44 0 1 0 0-122.88 61.44 61.44 0 0 0 0 122.88z m0-512a72.31488 72.31488 0 0 0-71.76192 81.3056l25.72288 205.7216a46.40768 46.40768 0 0 0 92.07808 0l25.72288-205.74208A72.31488 72.31488 0 0 0 512 307.2z" p-id="2750">
</path>
</svg>`,Pe=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg t="1652445277062" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="24" height="24">
<path d="M554.666667 810.666667v85.333333h-85.333334v-85.333333h85.333334zM170.666667 178.005333a42.666667 42.666667 0 0 1 34.986666 18.218667l203.904 291.328a42.666667 42.666667 0 0 1 0 48.896l-203.946666 291.328A42.666667 42.666667 0 0 1 128 803.328V220.672a42.666667 42.666667 0 0 1 42.666667-42.666667z m682.666666 0a42.666667 42.666667 0 0 1 42.368 37.717334l0.298667 4.949333v582.656a42.666667 42.666667 0 0 1-74.24 28.629333l-3.413333-4.181333-203.904-291.328a42.666667 42.666667 0 0 1-3.029334-43.861333l3.029334-5.034667 203.946666-291.328A42.666667 42.666667 0 0 1 853.333333 178.005333zM554.666667 640v85.333333h-85.333334v-85.333333h85.333334zM196.266667 319.104V716.8L335.957333 512 196.309333 319.104zM554.666667 469.333333v85.333334h-85.333334v-85.333334h85.333334z m0-170.666666v85.333333h-85.333334V298.666667h85.333334z m0-170.666667v85.333333h-85.333334V128h85.333334z">
</path>
</svg>
`,Fe=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg class="icon" width="22" height="22" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
<path d="M768 298.666667h170.666667v85.333333h-256V128h85.333333v170.666667zM341.333333 384H85.333333V298.666667h170.666667V128h85.333333v256z m426.666667 341.333333v170.666667h-85.333333v-256h256v85.333333h-170.666667zM341.333333 640v256H256v-170.666667H85.333333v-85.333333h256z" />
</svg>
`,Ie=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg class="icon" width="22" height="22" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
<path d="M625.777778 256h142.222222V398.222222h113.777778V142.222222H625.777778v113.777778zM256 398.222222V256H398.222222v-113.777778H142.222222V398.222222h113.777778zM768 625.777778v142.222222H625.777778v113.777778h256V625.777778h-113.777778zM398.222222 768H256V625.777778h-113.777778v256H398.222222v-113.777778z" />
</svg>
`,Le=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg class="icon" width="18" height="18" viewBox="0 0 1152 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
<path d="M1075.2 0H76.8A76.8 76.8 0 0 0 0 76.8v870.4A76.8 76.8 0 0 0 76.8 1024h998.4a76.8 76.8 0 0 0 76.8-76.8V76.8A76.8 76.8 0 0 0 1075.2 0zM1024 128v768H128V128h896zM896 512a64 64 0 0 1 7.488 127.552L896 640h-128v128a64 64 0 0 1-56.512 63.552L704 832a64 64 0 0 1-63.552-56.512L640 768V582.592c0-34.496 25.024-66.112 61.632-70.208L709.632 512H896zM256 512a64 64 0 0 1-7.488-127.552L256 384h128V256a64 64 0 0 1 56.512-63.552L448 192a64 64 0 0 1 63.552 56.512L512 256v185.408c0 34.432-25.024 66.112-61.632 70.144L442.368 512H256z" />
</svg>
`,Re=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg class="icon" width="18" height="18" viewBox="0 0 1152 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
<path d="M1075.2 0H76.8A76.8 76.8 0 0 0 0 76.8v870.4A76.8 76.8 0 0 0 76.8 1024h998.4a76.8 76.8 0 0 0 76.8-76.8V76.8A76.8 76.8 0 0 0 1075.2 0zM1024 128v768H128V128h896zM448 192a64 64 0 0 1 7.488 127.552L448 320H320v128a64 64 0 0 1-56.512 63.552L256 512a64 64 0 0 1-63.552-56.512L192 448V262.592c0-34.432 25.024-66.112 61.632-70.144L261.632 192H448zM704 832a64 64 0 0 1-7.488-127.552L704 704h128V576a64 64 0 0 1 56.512-63.552L896 512a64 64 0 0 1 63.552 56.512L960 576v185.408c0 34.496-25.024 66.112-61.632 70.208l-8 0.384H704z" />
</svg>
`,ze=`<svg xmlns="http://www.w3.org/2000/svg" width="50px" height="50px" viewBox="0 0 100 100" preserveAspectRatio="xMidYMid" class="uil-default">
  <rect x="0" y="0" width="100" height="100" fill="none" class="bk"/>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(0 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-1s" repeatCount="indefinite"/>
  </rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(30 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.9166666666666666s" repeatCount="indefinite"/>
  </rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(60 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.8333333333333334s" repeatCount="indefinite"/>
  </rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(90 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.75s" repeatCount="indefinite"/></rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(120 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.6666666666666666s" repeatCount="indefinite"/>
  </rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(150 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.5833333333333334s" repeatCount="indefinite"/>
  </rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(180 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.5s" repeatCount="indefinite"/></rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(210 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.4166666666666667s" repeatCount="indefinite"/>
  </rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(240 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.3333333333333333s" repeatCount="indefinite"/>
  </rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(270 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.25s" repeatCount="indefinite"/></rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(300 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.16666666666666666s" repeatCount="indefinite"/>
  </rect>
  <rect x="47" y="40" width="6" height="20" rx="5" ry="5" transform="rotate(330 50 50) translate(0 -30)">
    <animate attributeName="opacity" from="1" to="0" dur="1s" begin="-0.08333333333333333s" repeatCount="indefinite"/>
  </rect>
</svg>`,Be=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg t="1650612139149" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="20" height="20">
<path d="M298.666667 426.666667V341.333333a213.333333 213.333333 0 1 1 426.666666 0v85.333334h42.666667a85.333333 85.333333 0 0 1 85.333333 85.333333v256a85.333333 85.333333 0 0 1-85.333333 85.333333H256a85.333333 85.333333 0 0 1-85.333333-85.333333v-256a85.333333 85.333333 0 0 1 85.333333-85.333333h42.666667z m213.333333-213.333334a128 128 0 0 0-128 128v85.333334h256V341.333333a128 128 0 0 0-128-128z"></path>
</svg>
`,Ve=`<svg xmlns="http://www.w3.org/2000/svg" height="22" width="22" viewBox="0 0 22 22">
    <path d="M7 3a2 2 0 0 0-2 2v12a2 2 0 1 0 4 0V5a2 2 0 0 0-2-2zM15 3a2 2 0 0 0-2 2v12a2 2 0 1 0 4 0V5a2 2 0 0 0-2-2z"></path>
</svg>`,He=`<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="22" height="22">
<path d="M844.8 219.648h-665.6c-6.144 0-10.24 4.608-10.24 10.752v563.2c0 5.632 4.096 10.24 10.24 10.24h256v92.16h-256a102.4 102.4 0 0 1-102.4-102.4v-563.2c0-56.832 45.568-102.4 102.4-102.4h665.6a102.4 102.4 0 0 1 102.4 102.4v204.8h-92.16v-204.8c0-6.144-4.608-10.752-10.24-10.752zM614.4 588.8c-28.672 0-51.2 22.528-51.2 51.2v204.8c0 28.16 22.528 51.2 51.2 51.2h281.6c28.16 0 51.2-23.04 51.2-51.2v-204.8c0-28.672-23.04-51.2-51.2-51.2H614.4z"></path>
</svg>`,Ue=`<svg xmlns="http://www.w3.org/2000/svg" height="22" width="22" viewBox="0 0 22 22">
  <path d="M17.982 9.275L8.06 3.27A2.013 2.013 0 0 0 5 4.994v12.011a2.017 2.017 0 0 0 3.06 1.725l9.922-6.005a2.017 2.017 0 0 0 0-3.45z"></path>
</svg>`,We=`<svg height="24" viewBox="0 0 24 24" width="24"><path d="M10,8v8l6-4L10,8L10,8z M6.3,5L5.7,4.2C7.2,3,9,2.2,11,2l0.1,1C9.3,3.2,7.7,3.9,6.3,5z            M5,6.3L4.2,5.7C3,7.2,2.2,9,2,11 l1,.1C3.2,9.3,3.9,7.7,5,6.3z            M5,17.7c-1.1-1.4-1.8-3.1-2-4.8L2,13c0.2,2,1,3.8,2.2,5.4L5,17.7z            M11.1,21c-1.8-0.2-3.4-0.9-4.8-2 l-0.6,.8C7.2,21,9,21.8,11,22L11.1,21z            M22,12c0-5.2-3.9-9.4-9-10l-0.1,1c4.6,.5,8.1,4.3,8.1,9s-3.5,8.5-8.1,9l0.1,1 C18.2,21.5,22,17.2,22,12z" data-darkreader-inline-fill="" style="--darkreader-inline-fill:#a8a6a4;"></path></svg>`,Ge=`<svg xmlns="http://www.w3.org/2000/svg" height="22" width="22" viewBox="0 0 50 50">
	<path d="M 19.402344 6 C 17.019531 6 14.96875 7.679688 14.5 10.011719 L 14.097656 12 L 9 12 C 6.238281 12 4 14.238281 4 17 L 4 38 C 4 40.761719 6.238281 43 9 43 L 41 43 C 43.761719 43 46 40.761719 46 38 L 46 17 C 46 14.238281 43.761719 12 41 12 L 35.902344 12 L 35.5 10.011719 C 35.03125 7.679688 32.980469 6 30.597656 6 Z M 25 17 C 30.519531 17 35 21.480469 35 27 C 35 32.519531 30.519531 37 25 37 C 19.480469 37 15 32.519531 15 27 C 15 21.480469 19.480469 17 25 17 Z M 25 19 C 20.589844 19 17 22.589844 17 27 C 17 31.410156 20.589844 35 25 35 C 29.410156 35 33 31.410156 33 27 C 33 22.589844 29.410156 19 25 19 Z "/>
</svg>
`,Ke=`<svg xmlns="http://www.w3.org/2000/svg" height="22" width="22" viewBox="0 0 22 22">
    <circle cx="11" cy="11" r="2"></circle>
    <path d="M19.164 8.861L17.6 8.6a6.978 6.978 0 0 0-1.186-2.099l.574-1.533a1 1 0 0 0-.436-1.217l-1.997-1.153a1.001 1.001 0 0 0-1.272.23l-1.008 1.225a7.04 7.04 0 0 0-2.55.001L8.716 2.829a1 1 0 0 0-1.272-.23L5.447 3.751a1 1 0 0 0-.436 1.217l.574 1.533A6.997 6.997 0 0 0 4.4 8.6l-1.564.261A.999.999 0 0 0 2 9.847v2.306c0 .489.353.906.836.986l1.613.269a7 7 0 0 0 1.228 2.075l-.558 1.487a1 1 0 0 0 .436 1.217l1.997 1.153c.423.244.961.147 1.272-.23l1.04-1.263a7.089 7.089 0 0 0 2.272 0l1.04 1.263a1 1 0 0 0 1.272.23l1.997-1.153a1 1 0 0 0 .436-1.217l-.557-1.487c.521-.61.94-1.31 1.228-2.075l1.613-.269a.999.999 0 0 0 .835-.986V9.847a.999.999 0 0 0-.836-.986zM11 15a4 4 0 1 1 0-8 4 4 0 0 1 0 8z"></path>
</svg>`,qe=`<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 24 24">
<path d="M9.5 9.325v5.35q0 .575.525.875t1.025-.05l4.15-2.65q.475-.3.475-.85t-.475-.85L11.05 8.5q-.5-.35-1.025-.05t-.525.875ZM12 22q-2.075 0-3.9-.788t-3.175-2.137q-1.35-1.35-2.137-3.175T2 12q0-2.075.788-3.9t2.137-3.175q1.35-1.35 3.175-2.137T12 2q2.075 0 3.9.788t3.175 2.137q1.35 1.35 2.138 3.175T22 12q0 2.075-.788 3.9t-2.137 3.175q-1.35 1.35-3.175 2.138T12 22Z"/>
</svg>
`,Je=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg class="icon" width="26" height="26" viewBox="0 0 1740 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
    <path d="M511.8976 1024h670.5152c282.4192-0.4096 511.1808-229.4784 511.1808-511.8976 0-282.4192-228.7616-511.488-511.1808-511.8976H511.8976C229.4784 0.6144 0.7168 229.6832 0.7168 512.1024c0 282.4192 228.7616 511.488 511.1808 511.8976zM511.3344 48.64A464.5888 464.5888 0 1 1 48.0256 513.024 463.872 463.872 0 0 1 511.3344 48.4352V48.64z" />
</svg>
`,Ye=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg class="icon" width="26" height="26" viewBox="0 0 1664 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
    <path fill="#648FFC" d="M1152 0H512a512 512 0 0 0 0 1024h640a512 512 0 0 0 0-1024z m0 960a448 448 0 1 1 448-448 448 448 0 0 1-448 448z"  />
</svg>`,Xe=`<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg t="1650612464266" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="20" height="20"><path d="M666.752 194.517333L617.386667 268.629333A128 128 0 0 0 384 341.333333l0.042667 85.333334h384a85.333333 85.333333 0 0 1 85.333333 85.333333v256a85.333333 85.333333 0 0 1-85.333333 85.333333H256a85.333333 85.333333 0 0 1-85.333333-85.333333v-256a85.333333 85.333333 0 0 1 85.333333-85.333333h42.666667V341.333333a213.333333 213.333333 0 0 1 368.085333-146.816z"></path></svg>
`,Ze=`<svg xmlns="http://www.w3.org/2000/svg" height="22" width="22" viewBox="0 0 22 22">
    <path d="M15 11a3.998 3.998 0 0 0-2-3.465v2.636l1.865 1.865A4.02 4.02 0 0 0 15 11z"></path>
    <path d="M13.583 5.583A5.998 5.998 0 0 1 17 11a6 6 0 0 1-.585 2.587l1.477 1.477a8.001 8.001 0 0 0-3.446-11.286 1 1 0 0 0-.863 1.805zM18.778 18.778l-2.121-2.121-1.414-1.414-1.415-1.415L13 13l-2-2-3.889-3.889-3.889-3.889a.999.999 0 1 0-1.414 1.414L5.172 8H5a2 2 0 0 0-2 2v2a2 2 0 0 0 2 2h1l4.188 3.35a.5.5 0 0 0 .812-.39v-3.131l2.587 2.587-.01.005a1 1 0 0 0 .86 1.806c.215-.102.424-.214.627-.333l2.3 2.3a1.001 1.001 0 0 0 1.414-1.416zM11 5.04a.5.5 0 0 0-.813-.39L8.682 5.854 11 8.172V5.04z"></path>
</svg>`,Qe=`<svg xmlns="http://www.w3.org/2000/svg" height="22" width="22" viewBox="0 0 22 22">
    <path d="M10.188 4.65L6 8H5a2 2 0 0 0-2 2v2a2 2 0 0 0 2 2h1l4.188 3.35a.5.5 0 0 0 .812-.39V5.04a.498.498 0 0 0-.812-.39zM14.446 3.778a1 1 0 0 0-.862 1.804 6.002 6.002 0 0 1-.007 10.838 1 1 0 0 0 .86 1.806A8.001 8.001 0 0 0 19 11a8.001 8.001 0 0 0-4.554-7.222z"></path>
    <path d="M15 11a3.998 3.998 0 0 0-2-3.465v6.93A3.998 3.998 0 0 0 15 11z"></path>
</svg>`,$e=class{constructor(e){let t={loading:ze,state:qe,play:Ue,pause:Ve,check:Ae,volume:Qe,volumeClose:Ze,screenshot:Ge,setting:Ke,pip:He,arrowLeft:De,arrowRight:Oe,playbackRate:We,aspectRatio:ke,config:Me,lock:Be,flip:Pe,unlock:Xe,fullscreenOff:Fe,fullscreenOn:Ie,fullscreenWebOff:Le,fullscreenWebOn:Re,switchOn:Ye,switchOff:Je,error:Ne,close:je,airplay:Ee,...e.option.icons};for(let e in t)D(this,e,{get:()=>Et(e,t[e])})}},et=class extends P{constructor(e){super(e),this.name=`info`,T||this.init()}init(){let{proxy:e,constructor:t,template:{$infoPanel:n,$infoClose:r,$video:i}}=this.art;e(r,`click`,()=>{this.show=!1});let a=null,o=yt(`[data-video]`,n)||[];this.art.on(`destroy`,()=>clearTimeout(a));function s(){for(let e=0;e<o.length;e++){let t=o[e],n=i[t.dataset.video],r=typeof n==`number`?n.toFixed(2):n;t.textContent!==r&&(t.textContent=r)}a=setTimeout(s,t.INFO_LOOP_TIME)}s()}},tt=class extends P{constructor(e){super(e);let{option:t,template:{$layer:n}}=e;this.name=`layer`,this.$parent=n;for(let e=0;e<t.layers.length;e++)this.add(t.layers[e])}},nt=class extends P{constructor(e){super(e),this.name=`loading`,U(e.template.$loading,e.icons.loading)}},rt=class extends P{constructor(e){super(e),this.name=`mask`;let{template:t,icons:n,events:r}=e,i=U(t.$state,n.state),a=U(t.$state,n.error);W(a,`display`,`none`),e.on(`destroy`,()=>{W(i,`display`,`none`),W(a,`display`,null)}),r.proxy(t.$state,`click`,()=>e.play())}},it=class{constructor(e){this.art=e,this.timer=null,e.on(`destroy`,()=>this.destroy())}destroy(){this.timer&&(clearTimeout(this.timer),this.timer=null)}set show(e){let{constructor:t,template:{$player:n,$noticeInner:r}}=this.art;e?(r.textContent=e instanceof Error?e.message.trim():e,B(n,`art-notice-show`),clearTimeout(this.timer),this.timer=setTimeout(()=>{r.textContent=``,V(n,`art-notice-show`)},t.NOTICE_TIME)):V(n,`art-notice-show`)}get show(){let{template:{$player:e}}=this.art;return e.classList.contains(`art-notice-show`)}},at=[[`requestFullscreen`,`exitFullscreen`,`fullscreenElement`,`fullscreenEnabled`,`fullscreenchange`,`fullscreenerror`],[`webkitRequestFullscreen`,`webkitExitFullscreen`,`webkitFullscreenElement`,`webkitFullscreenEnabled`,`webkitfullscreenchange`,`webkitfullscreenerror`],[`webkitRequestFullScreen`,`webkitCancelFullScreen`,`webkitCurrentFullScreenElement`,`webkitCancelFullScreen`,`webkitfullscreenchange`,`webkitfullscreenerror`],[`mozRequestFullScreen`,`mozCancelFullScreen`,`mozFullScreenElement`,`mozFullScreenEnabled`,`mozfullscreenchange`,`mozfullscreenerror`],[`msRequestFullscreen`,`msExitFullscreen`,`msFullscreenElement`,`msFullscreenEnabled`,`MSFullscreenChange`,`MSFullscreenError`]],F=(()=>{if(typeof document>`u`)return!1;let e=at[0],t={};for(let n of at)if(n[1]in document){for(let[r,i]of n.entries())t[e[r]]=i;return t}return!1})(),ot={change:F.fullscreenchange,error:F.fullscreenerror},I={request(e=document.documentElement,t){return new Promise((n,r)=>{let i=()=>{I.off(`change`,i),n()};I.on(`change`,i);let a=e[F.requestFullscreen](t);a instanceof Promise&&a.then(i).catch(r)})},exit(){return new Promise((e,t)=>{if(!I.isFullscreen){e();return}let n=()=>{I.off(`change`,n),e()};I.on(`change`,n);let r=document[F.exitFullscreen]();r instanceof Promise&&r.then(n).catch(t)})},toggle(e,t){return I.isFullscreen?I.exit():I.request(e,t)},onchange(e){I.on(`change`,e)},onerror(e){I.on(`error`,e)},on(e,t){let n=ot[e];n&&document.addEventListener(n,t,!1)},off(e,t){let n=ot[e];n&&document.removeEventListener(n,t,!1)},raw:F},Object.defineProperties(I,{isFullscreen:{get:()=>!!document[F.fullscreenElement]},element:{enumerable:!0,get:()=>document[F.fullscreenElement]},isEnabled:{enumerable:!0,get:()=>!!document[F.fullscreenEnabled]}}),st=class{constructor(e){nr(e),Cn(e),Un(e),In(e),er(e),Jn(e),rr(e),Dn(e),On(e),Zn(e),Bn(e),Sn(e),qn(e),jn(e),Mn(e),zn(e),Nn(e),Vn(e),Hn(e),Tn(e),Kn(e),An(e),Pn(e),Wn(e),wn(e),En(e),Qn(e),tr(e),Yn(e),Xn(e),xn(e),Gn(e),$n(e),kn(e),Fn(e)}},ct=class{constructor(e){this.art=e,this.id=0;let{option:t}=e;t.miniProgressBar&&!t.isLive&&this.add(cr),t.lock&&T&&this.add(sr),t.autoPlayback&&!t.isLive&&this.add(ar),t.autoOrientation&&T&&this.add(ir),t.fastForward&&T&&!t.isLive&&this.add(or);for(let e=0;e<t.plugins.length;e++)this.add(t.plugins[e])}add(e){this.id+=1;let t=e.call(this.art,this.art);return t instanceof Promise?t.then(t=>this.next(e,t)):this.next(e,t)}next(e,t){let n=t&&t.name||e.name||`plugin${this.id}`;return X(!It(this,n),`Cannot add a plugin that already has the same name: ${n}`),D(this,n,{value:t}),this}},lt=class extends P{constructor(e){super(e);let{option:t,controls:n,template:{$setting:r}}=e;this.name=`setting`,this.$parent=r,this.id=0,this.active=null,this.cache=new Map,this.option=[...this.builtin,...t.settings],t.setting&&(this.format(),this.render(),e.on(`blur`,()=>{this.show&&(this.show=!1,this.render())}),e.on(`focus`,e=>{let t=q(e,n.setting),r=q(e,this.$parent);this.show&&!t&&!r&&(this.show=!1,this.render())}),e.on(`resize`,()=>this.resize()))}get builtin(){let e=[],{option:t}=this.art;return t.playbackRate&&e.push(dr(this.art)),t.aspectRatio&&e.push(lr(this.art)),t.flip&&e.push(ur(this.art)),t.subtitleOffset&&e.push(fr(this.art)),e}traverse(e,t=this.option){for(let n=0;n<t.length;n++){let r=t[n];e(r),r.selector?.length&&this.traverse(e,r.selector)}}check(e){e&&(e.$parent.tooltip=e.html,this.traverse(t=>{t.default=t===e,t.default&&t.$item&&G(t.$item,`art-current`)},e.$option),this.render(e.$parents))}format(e=this.option,t,n,r=[]){for(let i=0;i<e.length;i++){let a=e[i];if(a?.name?(X(!r.includes(a.name),`The [${a.name}] already exists in [setting]`),r.push(a.name)):a.name=`setting-${this.id++}`,!a.$formatted){D(a,`$parent`,{get:()=>t}),D(a,`$parents`,{get:()=>n}),D(a,`$option`,{get:()=>e});let r=[];D(a,`$events`,{get:()=>r}),D(a,`$formatted`,{get:()=>!0})}this.format(a.selector||[],a,e,r)}this.option=e}find(e=``){let t=null;return this.traverse(n=>{n.name===e&&(t=n)}),t}resize(){let{controls:e,constructor:{SETTING_WIDTH:t,SETTING_ITEM_HEIGHT:n},template:{$player:r,$setting:i}}=this.art;if(e.setting&&this.show){let a=this.active[0]?.$parent?.width||t,{left:o,width:s}=Y(e.setting),{left:c,width:l}=Y(r),u=o-c+s/2-a/2;if(W(i,`height`,`${this.active===this.option?this.active.length*n:(this.active.length+1)*n}px`),W(i,`width`,`${a}px`),this.art.isRotate||T)return;u+a>l?(W(i,`left`,null),W(i,`right`,null)):(W(i,`left`,`${u}px`),W(i,`right`,`auto`))}}inactivate(e){for(let t=0;t<e.$events.length;t++)this.art.events.remove(e.$events[t]);e.$events.length=0}remove(e){let t=this.find(e);X(t,`Can't find [${e}] in the [setting]`);let n=t.$option.indexOf(t);t.$option.splice(n,1),this.inactivate(t),t.$item&&bt(t.$item),this.render()}update(e){let t=this.find(e.name);return t?(this.inactivate(t),Object.assign(t,e),this.format(),this.createItem(t,!0),this.render(),t):this.add(e)}add(e,t=this.option){return t.push(e),this.format(),this.createItem(e),this.render(),e}createHeader(e){if(!this.cache.has(e.$option))return;let t=this.cache.get(e.$option),{proxy:n,icons:{arrowLeft:r},constructor:{SETTING_ITEM_HEIGHT:i}}=this.art,a=J(`div`);W(a,`height`,`${i}px`),B(a,`art-setting-item`),B(a,`art-setting-item-back`);let o=U(a,`<div class="art-setting-item-left"></div>`),s=J(`div`);B(s,`art-setting-item-left-icon`),U(s,r),U(o,s),U(o,e.$parent.html);let c=n(a,`click`,()=>this.render(e.$parents));e.$parent.$events.push(c),U(t,a)}createItem(e,t=!1){if(!this.cache.has(e.$option))return;let n=this.cache.get(e.$option),r=e.$item,i=`selector`;It(e,`switch`)&&(i=`switch`),It(e,`range`)&&(i=`range`),It(e,`onClick`)&&(i=`button`);let{icons:a,proxy:o,constructor:s}=this.art,c=J(`div`);B(c,`art-setting-item`),W(c,`height`,`${s.SETTING_ITEM_HEIGHT}px`),c.dataset.name=e.name||``,c.dataset.value=e.value||``;let l=U(c,`<div class="art-setting-item-left"></div>`),u=U(c,`<div class="art-setting-item-right"></div>`),d=J(`div`);switch(B(d,`art-setting-item-left-icon`),i){case`button`:case`switch`:case`range`:U(d,e.icon||a.config);break;case`selector`:e.selector?.length?U(d,e.icon||a.config):U(d,a.check);break}U(l,d),D(e,`$icon`,{configurable:!0,get:()=>d}),D(e,`icon`,{configurable:!0,get(){return d.innerHTML},set(e){d.innerHTML=``,U(d,e)}});let f=J(`div`);B(f,`art-setting-item-left-text`),U(f,e.html||``),U(l,f),D(e,`$html`,{configurable:!0,get:()=>f}),D(e,`html`,{configurable:!0,get(){return f.innerHTML},set(e){f.innerHTML=``,U(f,e)}});let p=J(`div`);switch(B(p,`art-setting-item-right-tooltip`),U(p,e.tooltip||``),U(u,p),D(e,`$tooltip`,{configurable:!0,get:()=>p}),D(e,`tooltip`,{configurable:!0,get(){return p.innerHTML},set(e){p.innerHTML=``,U(p,e)}}),i){case`switch`:{let t=J(`div`);B(t,`art-setting-item-right-icon`);let n=U(t,a.switchOn),r=U(t,a.switchOff);W(e.switch?r:n,`display`,`none`),U(u,t),D(e,`$switch`,{configurable:!0,get:()=>t});let i=e.switch;D(e,`switch`,{configurable:!0,get:()=>i,set(e){i=e,e?(W(r,`display`,`none`),W(n,`display`,null)):(W(r,`display`,null),W(n,`display`,`none`))}});break}case`range`:{let t=J(`div`);B(t,`art-setting-item-right-icon`);let n=U(t,`<input type="range">`);n.value=e.range[0],n.min=e.range[1],n.max=e.range[2],n.step=e.range[3],B(n,`art-setting-range`),U(u,t),D(e,`$range`,{configurable:!0,get:()=>n});let r=[...e.range];D(e,`range`,{configurable:!0,get:()=>r,set(e){r=[...e],n.value=e[0],n.min=e[1],n.max=e[2],n.step=e[3]}})}break;case`selector`:if(e.selector?.length){let e=J(`div`);B(e,`art-setting-item-right-icon`),U(e,a.arrowRight),U(u,e)}break}switch(i){case`switch`:if(e.onSwitch){let t=o(c,`click`,async t=>{e.switch=await e.onSwitch.call(this.art,e,c,t)});e.$events.push(t)}break;case`range`:if(e.$range){if(e.onRange){let t=o(e.$range,`change`,async t=>{e.range[0]=e.$range.valueAsNumber,e.tooltip=await e.onRange.call(this.art,e,c,t)});e.$events.push(t)}if(e.onChange){let t=o(e.$range,`input`,async t=>{e.range[0]=e.$range.valueAsNumber,e.tooltip=await e.onChange.call(this.art,e,c,t)});e.$events.push(t)}}break;case`selector`:{let t=o(c,`click`,async t=>{e.selector?.length?this.render(e.selector):(this.check(e),e.$parent.onSelect&&(e.$parent.tooltip=await e.$parent.onSelect.call(this.art,e,c,t)))});e.$events.push(t),e.default&&B(c,`art-current`)}break;case`button`:if(e.onClick){let t=o(c,`click`,async t=>{e.tooltip=await e.onClick.call(this.art,e,c,t)});e.$events.push(t)}break}D(e,`$item`,{configurable:!0,get:()=>c}),t?Tt(c,r):U(n,c),e.mounted&&setTimeout(()=>e.mounted.call(this.art,e.$item,e),0)}render(e=this.option){if(this.active=e,this.cache.has(e))G(this.cache.get(e),`art-current`);else{let t=J(`div`);this.cache.set(e,t),B(t,`art-setting-panel`),U(this.$parent,t),G(t,`art-current`),e[0]?.$parent&&this.createHeader(e[0]);for(let t=0;t<e.length;t++)this.createItem(e[t])}this.resize()}},ut=class{constructor(){this.name=`artplayer_settings`,this.settings={}}get(e){try{let t=JSON.parse(window.localStorage.getItem(this.name))||{};return e?t[e]:t}catch{return e?this.settings[e]:this.settings}}set(e,t){try{let n=Object.assign({},this.get(),{[e]:t});window.localStorage.setItem(this.name,JSON.stringify(n))}catch{this.settings[e]=t}}del(e){try{let t=this.get();delete t[e],window.localStorage.setItem(this.name,JSON.stringify(t))}catch{delete this.settings[e]}}clear(){try{window.localStorage.removeItem(this.name)}catch{this.settings={}}}},dt=`.art-video-player {
  --art-theme: #f00;
  --art-font-color: #fff;
  --art-background-color: #000;
  --art-text-shadow-color: rgba(0, 0, 0, 0.5);
  --art-transition-duration: 0.2s;
  --art-padding: 10px;
  --art-border-radius: 3px;
  --art-progress-height: 6px;
  --art-progress-color: rgba(255, 255, 255, 0.25);
  --art-progress-top-gap: 10px;
  --art-hover-color: rgba(255, 255, 255, 0.25);
  --art-loaded-color: rgba(255, 255, 255, 0.25);
  --art-state-size: 80px;
  --art-state-opacity: 0.8;
  --art-bottom-height: 100px;
  --art-bottom-offset: 20px;
  --art-bottom-gap: 5px;
  --art-highlight-width: 8px;
  --art-highlight-color: rgba(255, 255, 255, 0.5);
  --art-control-height: 46px;
  --art-control-opacity: 0.75;
  --art-control-icon-size: 36px;
  --art-control-icon-scale: 1.1;
  --art-volume-height: 120px;
  --art-volume-handle-size: 14px;
  --art-lock-size: 36px;
  --art-indicator-scale: 0;
  --art-indicator-size: 16px;
  --art-fullscreen-web-index: 9999;
  --art-settings-icon-size: 24px;
  --art-settings-max-height: 300px;
  --art-selector-max-height: 300px;
  --art-contextmenus-min-width: 250px;
  --art-subtitle-font-size: 20px;
  --art-subtitle-gap: 5px;
  --art-subtitle-bottom: 15px;
  --art-subtitle-border: #000;
  --art-widget-background: rgba(0, 0, 0, 0.85);
  --art-tip-background: rgba(0, 0, 0, 0.7);
  --art-scrollbar-size: 4px;
  --art-scrollbar-background: rgba(255, 255, 255, 0.25);
  --art-scrollbar-background-hover: rgba(255, 255, 255, 0.5);
  --art-mini-progress-height: 2px;
}
.art-bg-cover {
  background-position: center center;
  background-repeat: no-repeat;
  background-size: cover;
}
.art-bottom-gradient {
  background-image: linear-gradient(to top, #000, rgba(0, 0, 0, 0.4), transparent);
  background-repeat: repeat-x;
  background-position: center bottom;
}
.art-backdrop-filter {
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  backdrop-filter: saturate(180%) blur(20px);
  background-color: rgba(0, 0, 0, 0.75) !important;
}
.art-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.art-video-player {
  position: relative;
  margin: 0 auto;
  width: 100%;
  height: 100%;
  outline: 0;
  zoom: 1;
  padding: 0;
  text-align: left;
  direction: ltr;
  font-size: 14px;
  line-height: 1.3;
  user-select: none;
  box-sizing: border-box;
  color: var(--art-font-color);
  background-color: var(--art-background-color);
  text-shadow: 0 0 2px var(--art-text-shadow-color);
  font-family: PingFang SC, Helvetica Neue, Microsoft YaHei, Roboto, Arial, sans-serif;
  -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
  -ms-touch-action: manipulation;
  touch-action: manipulation;
  -ms-high-contrast-adjust: none;
}
.art-video-player *,
.art-video-player *::before,
.art-video-player *::after {
  box-sizing: border-box;
}
.art-video-player ::-webkit-scrollbar {
  width: var(--art-scrollbar-size);
  height: var(--art-scrollbar-size);
}
.art-video-player ::-webkit-scrollbar-thumb {
  background-color: var(--art-scrollbar-background);
}
.art-video-player ::-webkit-scrollbar-thumb:hover {
  background-color: var(--art-scrollbar-background-hover);
}
.art-video-player img {
  max-width: 100%;
  vertical-align: top;
}
.art-video-player svg {
  fill: var(--art-font-color);
}
.art-video-player a {
  color: var(--art-font-color);
  text-decoration: none;
}
.art-icon {
  line-height: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}
.art-video-player.art-backdrop .art-contextmenus,
.art-video-player.art-backdrop .art-info,
.art-video-player.art-backdrop .art-settings,
.art-video-player.art-backdrop .art-layer-auto-playback,
.art-video-player.art-backdrop .art-selector-list,
.art-video-player.art-backdrop .art-volume-inner {
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  backdrop-filter: saturate(180%) blur(20px);
  background-color: rgba(0, 0, 0, 0.75) !important;
}
.art-video {
  position: absolute;
  inset: 0;
  z-index: 10;
  width: 100%;
  height: 100%;
}
.art-poster {
  position: absolute;
  inset: 0;
  z-index: 11;
  width: 100%;
  height: 100%;
  background-position: center center;
  background-repeat: no-repeat;
  background-size: cover;
  pointer-events: none;
}
.art-video-player .art-subtitle {
  display: none;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  position: absolute;
  z-index: 20;
  width: 100%;
  padding: 0 5%;
  text-align: center;
  pointer-events: none;
  gap: var(--art-subtitle-gap);
  bottom: var(--art-subtitle-bottom);
  font-size: var(--art-subtitle-font-size);
  transition: bottom var(--art-transition-duration) ease;
  text-shadow: var(--art-subtitle-border) 1px 0 1px, var(--art-subtitle-border) 0 1px 1px, var(--art-subtitle-border) -1px 0 1px, var(--art-subtitle-border) 0 -1px 1px, var(--art-subtitle-border) 1px 1px 1px, var(--art-subtitle-border) -1px -1px 1px, var(--art-subtitle-border) 1px -1px 1px, var(--art-subtitle-border) -1px 1px 1px;
}
.art-video-player.art-subtitle-show .art-subtitle {
  display: flex;
}
.art-video-player.art-control-show .art-subtitle {
  bottom: calc(var(--art-control-height) + var(--art-subtitle-bottom));
}
.art-danmuku {
  position: absolute;
  inset: 0;
  z-index: 30;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}
.art-video-player .art-layers {
  position: absolute;
  inset: 0;
  z-index: 40;
  width: 100%;
  height: 100%;
  display: none;
  pointer-events: none;
}
.art-video-player .art-layers .art-layer {
  pointer-events: auto;
}
.art-video-player.art-layer-show .art-layers {
  display: flex;
}
.art-video-player .art-mask {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  inset: 0;
  z-index: 50;
  width: 100%;
  height: 100%;
  pointer-events: none;
}
.art-video-player .art-mask .art-state {
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transform: scale(2);
  width: var(--art-state-size);
  height: var(--art-state-size);
  transition: all var(--art-transition-duration) ease;
}
.art-video-player.art-mask-show .art-state {
  pointer-events: auto;
  opacity: var(--art-state-opacity);
  transform: scale(1);
}
.art-video-player.art-loading-show .art-state {
  display: none;
}
.art-video-player .art-loading {
  display: none;
  justify-content: center;
  align-items: center;
  position: absolute;
  inset: 0;
  z-index: 70;
  width: 100%;
  height: 100%;
  pointer-events: none;
}
.art-video-player.art-loading-show .art-loading {
  display: flex;
}
.art-video-player.art-loading-show .art-mask {
  display: none;
}
.art-video-player .art-bottom {
  position: absolute;
  inset: 0;
  z-index: 60;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  opacity: 0;
  overflow: hidden;
  pointer-events: none;
  padding: 0 var(--art-padding);
  transition: all var(--art-transition-duration) ease;
  background-size: 100% var(--art-bottom-height);
  background-image: linear-gradient(to top, #000, rgba(0, 0, 0, 0.4), transparent);
  background-repeat: repeat-x;
  background-position: center bottom;
}
.art-video-player .art-bottom .art-controls,
.art-video-player .art-bottom .art-progress {
  transform: translateY(var(--art-bottom-offset));
  transition: transform var(--art-transition-duration) ease;
}
.art-video-player.art-control-show .art-bottom,
.art-video-player.art-hover .art-bottom {
  opacity: 1;
}
.art-video-player.art-control-show .art-bottom .art-controls,
.art-video-player.art-hover .art-bottom .art-controls,
.art-video-player.art-control-show .art-bottom .art-progress,
.art-video-player.art-hover .art-bottom .art-progress {
  transform: translateY(0);
}
.art-bottom .art-progress {
  position: relative;
  z-index: 0;
  cursor: pointer;
  pointer-events: auto;
  padding-top: var(--art-progress-top-gap);
  padding-bottom: var(--art-bottom-gap);
}
.art-bottom .art-progress .art-control-progress {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: var(--art-progress-height);
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner {
  display: flex;
  align-items: center;
  position: relative;
  height: 50%;
  width: 100%;
  transition: height var(--art-transition-duration) ease;
  background-color: var(--art-progress-color);
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-hover {
  position: absolute;
  inset: 0;
  z-index: 0;
  width: 100%;
  height: 100%;
  width: 0%;
  background-color: var(--art-hover-color);
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-loaded {
  position: absolute;
  inset: 0;
  z-index: 10;
  width: 100%;
  height: 100%;
  width: 0%;
  background-color: var(--art-loaded-color);
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-played {
  position: absolute;
  inset: 0;
  z-index: 20;
  width: 100%;
  height: 100%;
  width: 0%;
  background-color: var(--art-theme);
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-highlight {
  position: absolute;
  inset: 0;
  z-index: 30;
  width: 100%;
  height: 100%;
  pointer-events: none;
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-highlight span {
  position: absolute;
  inset: 0;
  z-index: 0;
  width: 100%;
  height: 100%;
  right: auto;
  pointer-events: auto;
  width: var(--art-highlight-width) !important;
  transform: translateX(calc(var(--art-highlight-width) / -2));
  background-color: var(--art-highlight-color);
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-indicator {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  z-index: 40;
  left: 0;
  border-radius: 50%;
  width: var(--art-indicator-size);
  height: var(--art-indicator-size);
  transform: scale(var(--art-indicator-scale));
  margin-left: calc(var(--art-indicator-size) / -2);
  transition: transform var(--art-transition-duration) ease;
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-indicator .art-icon {
  width: 100%;
  height: 100%;
  pointer-events: none;
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-indicator:hover {
  transform: scale(1.2) !important;
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-indicator:active {
  transform: scale(1) !important;
}
.art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-tip {
  transform-origin: bottom center;
  transform: scale(0.5);
  opacity: 0;
  position: absolute;
  z-index: 50;
  top: -25px;
  left: 0;
  padding: 3px 5px;
  line-height: 1;
  font-size: 12px;
  border-radius: var(--art-border-radius);
  white-space: nowrap;
  background-color: var(--art-tip-background);
  transition: transform var(--art-transition-duration) ease, opacity var(--art-transition-duration) ease;
}
.art-bottom .art-progress .art-control-thumbnails {
  transform-origin: bottom center;
  transform: scale(0.5);
  opacity: 0;
  position: absolute;
  bottom: calc(var(--art-bottom-gap) + 10px);
  left: 0;
  border-radius: var(--art-border-radius);
  pointer-events: none;
  background-color: var(--art-widget-background);
  transition: transform var(--art-transition-duration) ease, opacity var(--art-transition-duration) ease;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.2), 0 1px 2px -1px rgba(0, 0, 0, 0.2);
}
.art-bottom .art-progress:hover .art-control-progress .art-control-progress-inner {
  height: 100%;
}
.art-bottom:hover .art-progress .art-control-progress .art-control-progress-inner .art-progress-indicator {
  transform: scale(1);
}
.art-progress-hover .art-bottom .art-progress .art-control-progress .art-control-progress-inner .art-progress-tip,
.art-progress-hover .art-bottom .art-progress .art-control-thumbnails {
  transform: scale(1);
  opacity: 1;
}
.art-video-player .art-controls {
  position: relative;
  z-index: 10;
  pointer-events: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--art-control-height);
}
.art-video-player .art-controls .art-controls-left,
.art-video-player .art-controls .art-controls-right {
  display: flex;
  height: 100%;
}
.art-video-player .art-controls .art-controls-center {
  display: none;
  justify-content: center;
  align-items: center;
  flex: 1;
  height: 100%;
  padding: 0 10px;
}
.art-video-player .art-controls .art-controls-right {
  justify-content: flex-end;
}
.art-video-player .art-controls .art-control {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  cursor: pointer;
  white-space: nowrap;
  opacity: var(--art-control-opacity);
  min-height: var(--art-control-height);
  min-width: var(--art-control-height);
  transition: opacity var(--art-transition-duration) ease;
}
.art-video-player .art-controls .art-control .art-icon {
  height: var(--art-control-icon-size);
  width: var(--art-control-icon-size);
  transform: scale(var(--art-control-icon-scale));
  transition: transform var(--art-transition-duration) ease;
}
.art-video-player .art-controls .art-control .art-icon:active {
  transform: scale(calc(var(--art-control-icon-scale) * 0.8));
}
.art-video-player .art-controls .art-control:hover {
  opacity: 1;
}
.art-control-volume {
  position: relative;
}
.art-control-volume .art-volume-panel {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  left: 0;
  right: 0;
  padding: 0 5px;
  font-size: 12px;
  text-align: center;
  cursor: default;
  opacity: 0;
  transform: translateY(10px);
  pointer-events: none;
  bottom: var(--art-control-height);
  width: var(--art-control-height);
  height: var(--art-volume-height);
  transition: all var(--art-transition-duration) ease;
}
.art-control-volume .art-volume-panel .art-volume-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  height: 100%;
  width: 100%;
  padding: 10px 0 12px;
  border-radius: var(--art-border-radius);
  background-color: var(--art-widget-background);
}
.art-control-volume .art-volume-panel .art-volume-inner .art-volume-slider {
  flex: 1;
  width: 100%;
  display: flex;
  cursor: pointer;
  position: relative;
  justify-content: center;
}
.art-control-volume .art-volume-panel .art-volume-inner .art-volume-slider .art-volume-handle {
  position: relative;
  display: flex;
  justify-content: center;
  width: 2px;
  border-radius: var(--art-border-radius);
  overflow: hidden;
  background-color: rgba(255, 255, 255, 0.25);
}
.art-control-volume .art-volume-panel .art-volume-inner .art-volume-slider .art-volume-handle .art-volume-loaded {
  position: absolute;
  inset: 0;
  z-index: 0;
  width: 100%;
  height: 100%;
  background-color: var(--art-theme);
}
.art-control-volume .art-volume-panel .art-volume-inner .art-volume-slider .art-volume-indicator {
  position: absolute;
  width: var(--art-volume-handle-size);
  height: var(--art-volume-handle-size);
  margin-top: calc(var(--art-volume-handle-size) / -2);
  flex-shrink: 0;
  transform: scale(1);
  border-radius: 100%;
  background-color: var(--art-theme);
  transition: transform var(--art-transition-duration) ease;
}
.art-control-volume .art-volume-panel .art-volume-inner .art-volume-slider:active .art-volume-indicator {
  transform: scale(0.9);
}
.art-control-volume:hover .art-volume-panel {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}
.art-video-player .art-notice {
  display: none;
  position: absolute;
  inset: 0;
  z-index: 80;
  width: 100%;
  height: 100%;
  height: auto;
  bottom: auto;
  padding: var(--art-padding);
  pointer-events: none;
}
.art-video-player .art-notice .art-notice-inner {
  display: inline-flex;
  padding: 5px;
  line-height: 1;
  border-radius: var(--art-border-radius);
  background-color: var(--art-tip-background);
}
.art-video-player.art-notice-show .art-notice {
  display: flex;
}
.art-video-player .art-contextmenus {
  display: none;
  flex-direction: column;
  position: absolute;
  z-index: 120;
  padding: 5px 0;
  border-radius: var(--art-border-radius);
  font-size: 12px;
  background-color: var(--art-widget-background);
  min-width: var(--art-contextmenus-min-width);
}
.art-video-player .art-contextmenus .art-contextmenu {
  cursor: pointer;
  display: flex;
  padding: 10px 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.art-video-player .art-contextmenus .art-contextmenu span {
  padding: 0 8px;
}
.art-video-player .art-contextmenus .art-contextmenu span:hover,
.art-video-player .art-contextmenus .art-contextmenu span.art-current {
  color: var(--art-theme);
}
.art-video-player .art-contextmenus .art-contextmenu:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
.art-video-player .art-contextmenus .art-contextmenu:last-child {
  border-bottom: none;
}
.art-video-player.art-contextmenu-show .art-contextmenus {
  display: flex;
}
.art-video-player .art-settings {
  display: none;
  flex-direction: column;
  position: absolute;
  z-index: 90;
  left: auto;
  overflow-y: auto;
  overflow-x: hidden;
  border-radius: var(--art-border-radius);
  max-height: var(--art-settings-max-height);
  right: var(--art-padding);
  bottom: var(--art-control-height);
  transition: all var(--art-transition-duration) ease;
  background-color: var(--art-widget-background);
}
.art-video-player .art-settings .art-setting-panel {
  display: none;
  flex-direction: column;
}
.art-video-player .art-settings .art-setting-panel.art-current {
  display: flex;
}
.art-video-player .art-settings .art-setting-panel .art-setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 5px;
  cursor: pointer;
  overflow: hidden;
  transition: background-color var(--art-transition-duration) ease;
}
.art-video-player .art-settings .art-setting-panel .art-setting-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
.art-video-player .art-settings .art-setting-panel .art-setting-item.art-current {
  color: var(--art-theme);
}
.art-video-player .art-settings .art-setting-panel .art-setting-item .art-icon-check {
  visibility: hidden;
  height: 15px;
}
.art-video-player .art-settings .art-setting-panel .art-setting-item.art-current .art-icon-check {
  visibility: visible;
}
.art-video-player .art-settings .art-setting-panel .art-setting-item .art-setting-item-left {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  gap: 5px;
}
.art-video-player .art-settings .art-setting-panel .art-setting-item .art-setting-item-left .art-setting-item-left-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  height: var(--art-settings-icon-size);
  width: var(--art-settings-icon-size);
}
.art-video-player .art-settings .art-setting-panel .art-setting-item .art-setting-item-right {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
  font-size: 12px;
}
.art-video-player .art-settings .art-setting-panel .art-setting-item .art-setting-item-right .art-setting-item-right-tooltip {
  white-space: nowrap;
  color: rgba(255, 255, 255, 0.5);
}
.art-video-player .art-settings .art-setting-panel .art-setting-item .art-setting-item-right .art-setting-item-right-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  min-width: 32px;
  height: 24px;
}
.art-video-player .art-settings .art-setting-panel .art-setting-item .art-setting-item-right .art-setting-range {
  height: 3px;
  width: 80px;
  outline: none;
  appearance: none;
  background-color: rgba(255, 255, 255, 0.2);
}
.art-video-player .art-settings .art-setting-panel .art-setting-item-back {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.art-video-player.art-setting-show .art-settings {
  display: flex;
}
.art-video-player .art-info {
  display: none;
  position: absolute;
  left: var(--art-padding);
  top: var(--art-padding);
  z-index: 100;
  padding: 10px;
  font-size: 12px;
  border-radius: var(--art-border-radius);
  background-color: var(--art-widget-background);
}
.art-video-player .art-info .art-info-panel {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.art-video-player .art-info .art-info-panel .art-info-item {
  display: flex;
  align-items: center;
  gap: 5px;
}
.art-video-player .art-info .art-info-panel .art-info-item .art-info-title {
  width: 100px;
  text-align: right;
}
.art-video-player .art-info .art-info-panel .art-info-item .art-info-content {
  width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  user-select: all;
}
.art-video-player .art-info .art-info-close {
  position: absolute;
  top: 5px;
  right: 5px;
  cursor: pointer;
}
.art-video-player.art-info-show .art-info {
  display: flex;
}
.art-hide-cursor * {
  cursor: none !important;
}
.art-video-player[data-aspect-ratio] {
  overflow: hidden;
}
.art-video-player[data-aspect-ratio] .art-video {
  object-fit: fill;
  box-sizing: content-box;
}
.art-fullscreen {
  --art-progress-height: 8px;
  --art-indicator-size: 20px;
  --art-control-height: 60px;
  --art-control-icon-scale: 1.3;
}
.art-fullscreen-web {
  --art-progress-height: 8px;
  --art-indicator-size: 20px;
  --art-control-height: 60px;
  --art-control-icon-scale: 1.3;
  position: fixed;
  inset: 0;
  z-index: var(--art-fullscreen-web-index);
  width: 100%;
  height: 100%;
}
.art-mini-popup {
  position: fixed;
  z-index: 9999;
  width: 320px;
  height: 180px;
  background: #000;
  border-radius: var(--art-border-radius);
  cursor: move;
  user-select: none;
  overflow: hidden;
  transition: opacity 0.2s ease;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}
.art-mini-popup svg {
  fill: #fff;
}
.art-mini-popup .art-video {
  pointer-events: none;
}
.art-mini-popup .art-mini-close {
  position: absolute;
  z-index: 20;
  right: 10px;
  top: 10px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s ease;
}
.art-mini-popup .art-mini-state {
  position: absolute;
  inset: 0;
  z-index: 30;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.2s ease;
  background-color: rgba(0, 0, 0, 0.25);
}
.art-mini-popup .art-mini-state .art-icon {
  opacity: 0.75;
  cursor: pointer;
  transform: scale(3);
  pointer-events: auto;
  transition: transform 0.2s ease;
}
.art-mini-popup .art-mini-state .art-icon:active {
  transform: scale(2.5);
}
.art-mini-popup.art-mini-dragging {
  opacity: 0.9;
}
.art-mini-popup:hover .art-mini-close,
.art-mini-popup:hover .art-mini-state {
  opacity: 1;
}
.art-video-player[data-flip='horizontal'] .art-video {
  transform: scaleX(-1);
}
.art-video-player[data-flip='vertical'] .art-video {
  transform: scaleY(-1);
}
.art-video-player .art-layer-lock {
  display: none;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 50%;
  border-radius: 50%;
  transform: translateY(-50%);
  height: var(--art-lock-size);
  width: var(--art-lock-size);
  left: var(--art-padding);
  background-color: var(--art-tip-background);
}
.art-video-player .art-layer-auto-playback {
  display: none;
  gap: 10px;
  align-items: center;
  position: absolute;
  border-radius: var(--art-border-radius);
  padding: 10px;
  line-height: 1;
  left: var(--art-padding);
  bottom: calc(var(--art-control-height) + var(--art-bottom-gap) + 10px);
  background-color: var(--art-widget-background);
}
.art-video-player .art-layer-auto-playback .art-auto-playback-close {
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
.art-video-player .art-layer-auto-playback .art-auto-playback-close svg {
  width: 15px;
  height: 15px;
  fill: var(--art-theme);
}
.art-video-player .art-layer-auto-playback .art-auto-playback-jump {
  color: var(--art-theme);
  cursor: pointer;
}
.art-video-player.art-lock .art-subtitle {
  bottom: var(--art-subtitle-bottom) !important;
}
.art-video-player.art-mini-progress-bar .art-bottom,
.art-video-player.art-lock .art-bottom {
  opacity: 1;
  padding: 0;
  background-image: none;
}
.art-video-player.art-mini-progress-bar .art-bottom .art-controls,
.art-video-player.art-lock .art-bottom .art-controls,
.art-video-player.art-mini-progress-bar .art-bottom .art-progress,
.art-video-player.art-lock .art-bottom .art-progress {
  transform: translateY(calc(var(--art-control-height) + var(--art-bottom-gap) + var(--art-progress-height) / 4));
}
.art-video-player.art-mini-progress-bar .art-bottom .art-progress-indicator,
.art-video-player.art-lock .art-bottom .art-progress-indicator {
  display: none !important;
}
.art-video-player.art-control-show .art-layer-lock {
  display: flex;
}
.art-control-selector {
  position: relative;
  display: flex;
  justify-content: center;
}
.art-control-selector .art-selector-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: absolute;
  border-radius: var(--art-border-radius);
  overflow-y: auto;
  overflow-x: hidden;
  opacity: 0;
  transform: translateY(10px);
  pointer-events: none;
  bottom: var(--art-control-height);
  max-height: var(--art-selector-max-height);
  background-color: var(--art-widget-background);
  transition: all var(--art-transition-duration) ease;
}
.art-control-selector .art-selector-list .art-selector-item {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  padding: 10px 15px;
  flex-shrink: 0;
  line-height: 1;
}
.art-control-selector .art-selector-list .art-selector-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
.art-control-selector .art-selector-list .art-selector-item:hover,
.art-control-selector .art-selector-list .art-selector-item.art-current {
  color: var(--art-theme);
}
.art-control-selector:hover .art-selector-list {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}
.art-video-player {
  /*! Hint.css - v2.7.0 - 2021-10-01
    * https://kushagra.dev/lab/hint/
    * Copyright (c) 2021 Kushagra Gour */
  /*-------------------------------------*\\
        HINT.css - A CSS tooltip library
    \\*-------------------------------------*/
  /**
    * HINT.css is a tooltip library made in pure CSS.
    *
    * Source: https://github.com/chinchang/hint.css
    * Demo: http://kushagragour.in/lab/hint/
    *
    */
  /**
    * source: hint-core.scss
    *
    * Defines the basic styling for the tooltip.
    * Each tooltip is made of 2 parts:
    * 	1) body (:after)
    * 	2) arrow (:before)
    *
    * Classes added:
    * 	1) hint
    */
  /**
    * source: hint-position.scss
    *
    * Defines the positoning logic for the tooltips.
    *
    * Classes added:
    * 	1) hint--top
    * 	2) hint--bottom
    * 	3) hint--left
    * 	4) hint--right
    */
  /**
    * set default color for tooltip arrows
    */
  /**
    * top tooltip
    */
  /**
    * bottom tooltip
    */
  /**
    * right tooltip
    */
  /**
    * left tooltip
    */
  /**
    * top-left tooltip
    */
  /**
    * top-right tooltip
    */
  /**
    * bottom-left tooltip
    */
  /**
    * bottom-right tooltip
    */
  /**
    * source: hint-sizes.scss
    *
    * Defines width restricted tooltips that can span
    * across multiple lines.
    *
    * Classes added:
    * 	1) hint--small
    * 	2) hint--medium
    * 	3) hint--large
    *
    */
  /**
    * source: hint-theme.scss
    *
    * Defines basic theme for tooltips.
    *
    */
  /**
    * source: hint-color-types.scss
    *
    * Contains tooltips of various types based on color differences.
    *
    * Classes added:
    * 	1) hint--error
    * 	2) hint--warning
    * 	3) hint--info
    * 	4) hint--success
    *
    */
  /**
    * Error
    */
  /**
    * Warning
    */
  /**
    * Info
    */
  /**
    * Success
    */
  /**
    * source: hint-always.scss
    *
    * Defines a persisted tooltip which shows always.
    *
    * Classes added:
    * 	1) hint--always
    *
    */
  /**
    * source: hint-rounded.scss
    *
    * Defines rounded corner tooltips.
    *
    * Classes added:
    * 	1) hint--rounded
    *
    */
  /**
    * source: hint-effects.scss
    *
    * Defines various transition effects for the tooltips.
    *
    * Classes added:
    * 	1) hint--no-animate
    * 	2) hint--bounce
    *
    */
}
.art-video-player [class*='hint--'] {
  position: relative;
  display: inline-block;
  font-style: normal;
  /**
        * tooltip arrow
        */
  /**
        * tooltip body
        */
}
.art-video-player [class*='hint--']:before,
.art-video-player [class*='hint--']:after {
  position: absolute;
  -webkit-transform: translate3d(0, 0, 0);
  -moz-transform: translate3d(0, 0, 0);
  transform: translate3d(0, 0, 0);
  visibility: hidden;
  opacity: 0;
  z-index: 1000000;
  pointer-events: none;
  -webkit-transition: 0.3s ease;
  -moz-transition: 0.3s ease;
  transition: 0.3s ease;
  -webkit-transition-delay: 0ms;
  -moz-transition-delay: 0ms;
  transition-delay: 0ms;
}
.art-video-player [class*='hint--']:hover:before,
.art-video-player [class*='hint--']:hover:after {
  visibility: visible;
  opacity: 1;
}
.art-video-player [class*='hint--']:hover:before,
.art-video-player [class*='hint--']:hover:after {
  -webkit-transition-delay: 100ms;
  -moz-transition-delay: 100ms;
  transition-delay: 100ms;
}
.art-video-player [class*='hint--']:before {
  content: '';
  position: absolute;
  background: transparent;
  border: 6px solid transparent;
  z-index: 1000001;
}
.art-video-player [class*='hint--']:after {
  background: #000000;
  color: white;
  padding: 8px 10px;
  font-size: 12px;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  line-height: 12px;
  white-space: nowrap;
}
.art-video-player [class*='hint--'][aria-label]:after {
  content: attr(aria-label);
}
.art-video-player [class*='hint--'][data-hint]:after {
  content: attr(data-hint);
}
.art-video-player [aria-label='']:before,
.art-video-player [aria-label='']:after,
.art-video-player [data-hint='']:before,
.art-video-player [data-hint='']:after {
  display: none !important;
}
.art-video-player .hint--top-left:before {
  border-top-color: #000000;
}
.art-video-player .hint--top-right:before {
  border-top-color: #000000;
}
.art-video-player .hint--top:before {
  border-top-color: #000000;
}
.art-video-player .hint--bottom-left:before {
  border-bottom-color: #000000;
}
.art-video-player .hint--bottom-right:before {
  border-bottom-color: #000000;
}
.art-video-player .hint--bottom:before {
  border-bottom-color: #000000;
}
.art-video-player .hint--left:before {
  border-left-color: #000000;
}
.art-video-player .hint--right:before {
  border-right-color: #000000;
}
.art-video-player .hint--top:before {
  margin-bottom: -11px;
}
.art-video-player .hint--top:before,
.art-video-player .hint--top:after {
  bottom: 100%;
  left: 50%;
}
.art-video-player .hint--top:before {
  left: calc(50% - 6px);
}
.art-video-player .hint--top:after {
  -webkit-transform: translateX(-50%);
  -moz-transform: translateX(-50%);
  transform: translateX(-50%);
}
.art-video-player .hint--top:hover:before {
  -webkit-transform: translateY(-8px);
  -moz-transform: translateY(-8px);
  transform: translateY(-8px);
}
.art-video-player .hint--top:hover:after {
  -webkit-transform: translateX(-50%) translateY(-8px);
  -moz-transform: translateX(-50%) translateY(-8px);
  transform: translateX(-50%) translateY(-8px);
}
.art-video-player .hint--bottom:before {
  margin-top: -11px;
}
.art-video-player .hint--bottom:before,
.art-video-player .hint--bottom:after {
  top: 100%;
  left: 50%;
}
.art-video-player .hint--bottom:before {
  left: calc(50% - 6px);
}
.art-video-player .hint--bottom:after {
  -webkit-transform: translateX(-50%);
  -moz-transform: translateX(-50%);
  transform: translateX(-50%);
}
.art-video-player .hint--bottom:hover:before {
  -webkit-transform: translateY(8px);
  -moz-transform: translateY(8px);
  transform: translateY(8px);
}
.art-video-player .hint--bottom:hover:after {
  -webkit-transform: translateX(-50%) translateY(8px);
  -moz-transform: translateX(-50%) translateY(8px);
  transform: translateX(-50%) translateY(8px);
}
.art-video-player .hint--right:before {
  margin-left: -11px;
  margin-bottom: -6px;
}
.art-video-player .hint--right:after {
  margin-bottom: -14px;
}
.art-video-player .hint--right:before,
.art-video-player .hint--right:after {
  left: 100%;
  bottom: 50%;
}
.art-video-player .hint--right:hover:before {
  -webkit-transform: translateX(8px);
  -moz-transform: translateX(8px);
  transform: translateX(8px);
}
.art-video-player .hint--right:hover:after {
  -webkit-transform: translateX(8px);
  -moz-transform: translateX(8px);
  transform: translateX(8px);
}
.art-video-player .hint--left:before {
  margin-right: -11px;
  margin-bottom: -6px;
}
.art-video-player .hint--left:after {
  margin-bottom: -14px;
}
.art-video-player .hint--left:before,
.art-video-player .hint--left:after {
  right: 100%;
  bottom: 50%;
}
.art-video-player .hint--left:hover:before {
  -webkit-transform: translateX(-8px);
  -moz-transform: translateX(-8px);
  transform: translateX(-8px);
}
.art-video-player .hint--left:hover:after {
  -webkit-transform: translateX(-8px);
  -moz-transform: translateX(-8px);
  transform: translateX(-8px);
}
.art-video-player .hint--top-left:before {
  margin-bottom: -11px;
}
.art-video-player .hint--top-left:before,
.art-video-player .hint--top-left:after {
  bottom: 100%;
  left: 50%;
}
.art-video-player .hint--top-left:before {
  left: calc(50% - 6px);
}
.art-video-player .hint--top-left:after {
  -webkit-transform: translateX(-100%);
  -moz-transform: translateX(-100%);
  transform: translateX(-100%);
}
.art-video-player .hint--top-left:after {
  margin-left: 12px;
}
.art-video-player .hint--top-left:hover:before {
  -webkit-transform: translateY(-8px);
  -moz-transform: translateY(-8px);
  transform: translateY(-8px);
}
.art-video-player .hint--top-left:hover:after {
  -webkit-transform: translateX(-100%) translateY(-8px);
  -moz-transform: translateX(-100%) translateY(-8px);
  transform: translateX(-100%) translateY(-8px);
}
.art-video-player .hint--top-right:before {
  margin-bottom: -11px;
}
.art-video-player .hint--top-right:before,
.art-video-player .hint--top-right:after {
  bottom: 100%;
  left: 50%;
}
.art-video-player .hint--top-right:before {
  left: calc(50% - 6px);
}
.art-video-player .hint--top-right:after {
  -webkit-transform: translateX(0);
  -moz-transform: translateX(0);
  transform: translateX(0);
}
.art-video-player .hint--top-right:after {
  margin-left: -12px;
}
.art-video-player .hint--top-right:hover:before {
  -webkit-transform: translateY(-8px);
  -moz-transform: translateY(-8px);
  transform: translateY(-8px);
}
.art-video-player .hint--top-right:hover:after {
  -webkit-transform: translateY(-8px);
  -moz-transform: translateY(-8px);
  transform: translateY(-8px);
}
.art-video-player .hint--bottom-left:before {
  margin-top: -11px;
}
.art-video-player .hint--bottom-left:before,
.art-video-player .hint--bottom-left:after {
  top: 100%;
  left: 50%;
}
.art-video-player .hint--bottom-left:before {
  left: calc(50% - 6px);
}
.art-video-player .hint--bottom-left:after {
  -webkit-transform: translateX(-100%);
  -moz-transform: translateX(-100%);
  transform: translateX(-100%);
}
.art-video-player .hint--bottom-left:after {
  margin-left: 12px;
}
.art-video-player .hint--bottom-left:hover:before {
  -webkit-transform: translateY(8px);
  -moz-transform: translateY(8px);
  transform: translateY(8px);
}
.art-video-player .hint--bottom-left:hover:after {
  -webkit-transform: translateX(-100%) translateY(8px);
  -moz-transform: translateX(-100%) translateY(8px);
  transform: translateX(-100%) translateY(8px);
}
.art-video-player .hint--bottom-right:before {
  margin-top: -11px;
}
.art-video-player .hint--bottom-right:before,
.art-video-player .hint--bottom-right:after {
  top: 100%;
  left: 50%;
}
.art-video-player .hint--bottom-right:before {
  left: calc(50% - 6px);
}
.art-video-player .hint--bottom-right:after {
  -webkit-transform: translateX(0);
  -moz-transform: translateX(0);
  transform: translateX(0);
}
.art-video-player .hint--bottom-right:after {
  margin-left: -12px;
}
.art-video-player .hint--bottom-right:hover:before {
  -webkit-transform: translateY(8px);
  -moz-transform: translateY(8px);
  transform: translateY(8px);
}
.art-video-player .hint--bottom-right:hover:after {
  -webkit-transform: translateY(8px);
  -moz-transform: translateY(8px);
  transform: translateY(8px);
}
.art-video-player .hint--small:after,
.art-video-player .hint--medium:after,
.art-video-player .hint--large:after {
  white-space: normal;
  line-height: 1.4em;
  word-wrap: break-word;
}
.art-video-player .hint--small:after {
  width: 80px;
}
.art-video-player .hint--medium:after {
  width: 150px;
}
.art-video-player .hint--large:after {
  width: 300px;
}
.art-video-player [class*='hint--'] {
  /**
        * tooltip body
        */
}
.art-video-player [class*='hint--']:after {
  text-shadow: 0 -1px 0px black;
  box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.3);
}
.art-video-player .hint--error:after {
  background-color: #b34e4d;
  text-shadow: 0 -1px 0px #592726;
}
.art-video-player .hint--error.hint--top-left:before {
  border-top-color: #b34e4d;
}
.art-video-player .hint--error.hint--top-right:before {
  border-top-color: #b34e4d;
}
.art-video-player .hint--error.hint--top:before {
  border-top-color: #b34e4d;
}
.art-video-player .hint--error.hint--bottom-left:before {
  border-bottom-color: #b34e4d;
}
.art-video-player .hint--error.hint--bottom-right:before {
  border-bottom-color: #b34e4d;
}
.art-video-player .hint--error.hint--bottom:before {
  border-bottom-color: #b34e4d;
}
.art-video-player .hint--error.hint--left:before {
  border-left-color: #b34e4d;
}
.art-video-player .hint--error.hint--right:before {
  border-right-color: #b34e4d;
}
.art-video-player .hint--warning:after {
  background-color: #c09854;
  text-shadow: 0 -1px 0px #6c5328;
}
.art-video-player .hint--warning.hint--top-left:before {
  border-top-color: #c09854;
}
.art-video-player .hint--warning.hint--top-right:before {
  border-top-color: #c09854;
}
.art-video-player .hint--warning.hint--top:before {
  border-top-color: #c09854;
}
.art-video-player .hint--warning.hint--bottom-left:before {
  border-bottom-color: #c09854;
}
.art-video-player .hint--warning.hint--bottom-right:before {
  border-bottom-color: #c09854;
}
.art-video-player .hint--warning.hint--bottom:before {
  border-bottom-color: #c09854;
}
.art-video-player .hint--warning.hint--left:before {
  border-left-color: #c09854;
}
.art-video-player .hint--warning.hint--right:before {
  border-right-color: #c09854;
}
.art-video-player .hint--info:after {
  background-color: #3986ac;
  text-shadow: 0 -1px 0px #1a3c4d;
}
.art-video-player .hint--info.hint--top-left:before {
  border-top-color: #3986ac;
}
.art-video-player .hint--info.hint--top-right:before {
  border-top-color: #3986ac;
}
.art-video-player .hint--info.hint--top:before {
  border-top-color: #3986ac;
}
.art-video-player .hint--info.hint--bottom-left:before {
  border-bottom-color: #3986ac;
}
.art-video-player .hint--info.hint--bottom-right:before {
  border-bottom-color: #3986ac;
}
.art-video-player .hint--info.hint--bottom:before {
  border-bottom-color: #3986ac;
}
.art-video-player .hint--info.hint--left:before {
  border-left-color: #3986ac;
}
.art-video-player .hint--info.hint--right:before {
  border-right-color: #3986ac;
}
.art-video-player .hint--success:after {
  background-color: #458746;
  text-shadow: 0 -1px 0px #1a321a;
}
.art-video-player .hint--success.hint--top-left:before {
  border-top-color: #458746;
}
.art-video-player .hint--success.hint--top-right:before {
  border-top-color: #458746;
}
.art-video-player .hint--success.hint--top:before {
  border-top-color: #458746;
}
.art-video-player .hint--success.hint--bottom-left:before {
  border-bottom-color: #458746;
}
.art-video-player .hint--success.hint--bottom-right:before {
  border-bottom-color: #458746;
}
.art-video-player .hint--success.hint--bottom:before {
  border-bottom-color: #458746;
}
.art-video-player .hint--success.hint--left:before {
  border-left-color: #458746;
}
.art-video-player .hint--success.hint--right:before {
  border-right-color: #458746;
}
.art-video-player .hint--always:after,
.art-video-player .hint--always:before {
  opacity: 1;
  visibility: visible;
}
.art-video-player .hint--always.hint--top:before {
  -webkit-transform: translateY(-8px);
  -moz-transform: translateY(-8px);
  transform: translateY(-8px);
}
.art-video-player .hint--always.hint--top:after {
  -webkit-transform: translateX(-50%) translateY(-8px);
  -moz-transform: translateX(-50%) translateY(-8px);
  transform: translateX(-50%) translateY(-8px);
}
.art-video-player .hint--always.hint--top-left:before {
  -webkit-transform: translateY(-8px);
  -moz-transform: translateY(-8px);
  transform: translateY(-8px);
}
.art-video-player .hint--always.hint--top-left:after {
  -webkit-transform: translateX(-100%) translateY(-8px);
  -moz-transform: translateX(-100%) translateY(-8px);
  transform: translateX(-100%) translateY(-8px);
}
.art-video-player .hint--always.hint--top-right:before {
  -webkit-transform: translateY(-8px);
  -moz-transform: translateY(-8px);
  transform: translateY(-8px);
}
.art-video-player .hint--always.hint--top-right:after {
  -webkit-transform: translateY(-8px);
  -moz-transform: translateY(-8px);
  transform: translateY(-8px);
}
.art-video-player .hint--always.hint--bottom:before {
  -webkit-transform: translateY(8px);
  -moz-transform: translateY(8px);
  transform: translateY(8px);
}
.art-video-player .hint--always.hint--bottom:after {
  -webkit-transform: translateX(-50%) translateY(8px);
  -moz-transform: translateX(-50%) translateY(8px);
  transform: translateX(-50%) translateY(8px);
}
.art-video-player .hint--always.hint--bottom-left:before {
  -webkit-transform: translateY(8px);
  -moz-transform: translateY(8px);
  transform: translateY(8px);
}
.art-video-player .hint--always.hint--bottom-left:after {
  -webkit-transform: translateX(-100%) translateY(8px);
  -moz-transform: translateX(-100%) translateY(8px);
  transform: translateX(-100%) translateY(8px);
}
.art-video-player .hint--always.hint--bottom-right:before {
  -webkit-transform: translateY(8px);
  -moz-transform: translateY(8px);
  transform: translateY(8px);
}
.art-video-player .hint--always.hint--bottom-right:after {
  -webkit-transform: translateY(8px);
  -moz-transform: translateY(8px);
  transform: translateY(8px);
}
.art-video-player .hint--always.hint--left:before {
  -webkit-transform: translateX(-8px);
  -moz-transform: translateX(-8px);
  transform: translateX(-8px);
}
.art-video-player .hint--always.hint--left:after {
  -webkit-transform: translateX(-8px);
  -moz-transform: translateX(-8px);
  transform: translateX(-8px);
}
.art-video-player .hint--always.hint--right:before {
  -webkit-transform: translateX(8px);
  -moz-transform: translateX(8px);
  transform: translateX(8px);
}
.art-video-player .hint--always.hint--right:after {
  -webkit-transform: translateX(8px);
  -moz-transform: translateX(8px);
  transform: translateX(8px);
}
.art-video-player .hint--rounded:after {
  border-radius: 4px;
}
.art-video-player .hint--no-animate:before,
.art-video-player .hint--no-animate:after {
  -webkit-transition-duration: 0ms;
  -moz-transition-duration: 0ms;
  transition-duration: 0ms;
}
.art-video-player .hint--bounce:before,
.art-video-player .hint--bounce:after {
  -webkit-transition: opacity 0.3s ease, visibility 0.3s ease, -webkit-transform 0.3s cubic-bezier(0.71, 1.7, 0.77, 1.24);
  -moz-transition: opacity 0.3s ease, visibility 0.3s ease, -moz-transform 0.3s cubic-bezier(0.71, 1.7, 0.77, 1.24);
  transition: opacity 0.3s ease, visibility 0.3s ease, transform 0.3s cubic-bezier(0.71, 1.7, 0.77, 1.24);
}
.art-video-player .hint--no-shadow:before,
.art-video-player .hint--no-shadow:after {
  text-shadow: initial;
  box-shadow: initial;
}
.art-video-player .hint--no-arrow:before {
  display: none;
}
.art-video-player.art-mobile {
  --art-bottom-gap: 10px;
  --art-control-height: 38px;
  --art-control-icon-scale: 1;
  --art-state-size: 60px;
  --art-settings-max-height: 180px;
  --art-selector-max-height: 180px;
  --art-indicator-scale: 1;
  --art-control-opacity: 1;
}
.art-video-player.art-mobile .art-controls-left {
  margin-left: calc(var(--art-padding) / -1);
}
.art-video-player.art-mobile .art-controls-right {
  margin-right: calc(var(--art-padding) / -1);
}
`,ft=class extends P{constructor(e){super(e),this.name=`subtitle`,this.option=null,this.destroyEvent=()=>null,this.init(e.option.subtitle);let t=!1;e.on(`video:timeupdate`,()=>{if(!this.url)return;let e=this.art.template.$video.webkitDisplayingFullscreen;typeof e==`boolean`&&e!==t&&(t=e,this.createTrack(e?`subtitles`:`metadata`,this.url))})}get url(){return this.art.template.$track.src}set url(e){this.switch(e)}get textTrack(){return this.art.template.$video?.textTracks?.[0]}get activeCues(){return this.textTrack?Array.from(this.textTrack.activeCues):[]}get cues(){return this.textTrack?Array.from(this.textTrack.cues):[]}style(e,t){let{$subtitle:n}=this.art.template;return typeof e==`object`?xt(n,e):W(n,e,t)}update(){let{option:{subtitle:e},template:{$subtitle:t}}=this.art;t.innerHTML=``,this.activeCues.length&&(this.art.emit(`subtitleBeforeUpdate`,this.activeCues),t.innerHTML=this.activeCues.map((t,n)=>t.text.split(/\r?\n/).filter(e=>e.trim()).map(t=>`<div class="art-subtitle-line" data-group="${n}">
                                ${e.escape?Pt(t):t}
                            </div>`).join(``)).join(``),this.art.emit(`subtitleAfterUpdate`,this.activeCues))}async switch(e,t={}){let{i18n:n,notice:r,option:i}=this.art,a={...i.subtitle,...t,url:e},o=await this.init(a);return t.name&&(r.show=`${n.get(`Switch Subtitle`)}: ${t.name}`),o}createTrack(e,t){let{template:n,proxy:r,option:i}=this.art,{$video:a,$track:o}=n,s=J(`track`);s.default=!0,s.kind=e,s.src=t,s.label=i.subtitle.name||`Artplayer`,s.track.mode=`hidden`,s.onload=()=>{this.art.emit(`subtitleLoad`,this.cues,this.option)},this.art.events.remove(this.destroyEvent),o.onload=null,bt(o),U(a,s),n.$track=s,this.destroyEvent=r(this.textTrack,`cuechange`,()=>this.update())}async init(e){let{notice:t,template:{$subtitle:n}}=this.art;if(!this.textTrack)return null;if(y(e,ye.subtitle),e.url)return this.option=e,this.style(e.style),fetch(e.url).then(e=>e.arrayBuffer()).then(t=>{let n=new TextDecoder(e.encoding).decode(t);switch(e.type||jt(e.url)){case`srt`:{let t=Bt(n);return Vt(e.onVttLoad(t))}case`ass`:{let t=Ht(n);return Vt(e.onVttLoad(t))}case`vtt`:return Vt(e.onVttLoad(n));default:return e.url}}).then(e=>(n.innerHTML=``,this.url===e?e:(URL.revokeObjectURL(this.url),this.createTrack(`metadata`,e),e))).catch(e=>{throw n.innerHTML=``,t.show=e,e})}},pt=class e{constructor(e){this.art=e;let{option:t,constructor:n}=e;t.container instanceof Element?this.$container=t.container:(this.$container=z(t.container),X(this.$container,`No container element found by ${t.container}`)),X(Ot(),`The current browser does not support flex layout`);let r=this.$container.tagName.toLowerCase();X(r===`div`,`Unsupported container element type, only support 'div' but got '${r}'`),X(n.instances.every(e=>e.template.$container!==this.$container),`Cannot mount multiple instances on the same dom element`),this.query=this.query.bind(this),this.$container.dataset.artId=e.id,this.init()}static get html(){return`
          <div class="art-video-player art-subtitle-show art-layer-show art-control-show art-mask-show">
            <video class="art-video">
              <track default kind="metadata" src=""></track>
            </video>
            <div class="art-poster"></div>
            <div class="art-subtitle"></div>
            <div class="art-danmuku"></div>
            <div class="art-layers"></div>
            <div class="art-mask">
              <div class="art-state"></div>
            </div>
            <div class="art-bottom">
              <div class="art-progress"></div>
              <div class="art-controls">
                <div class="art-controls-left"></div>
                <div class="art-controls-center"></div>
                <div class="art-controls-right"></div>
              </div>
            </div>
            <div class="art-loading"></div>
            <div class="art-notice">
              <div class="art-notice-inner"></div>
            </div>
            <div class="art-settings"></div>
            <div class="art-info">
              <div class="art-info-panel">
                <div class="art-info-item">
                  <div class="art-info-title">Player version:</div>
                  <div class="art-info-content">${b}</div>
                </div>
                <div class="art-info-item">
                  <div class="art-info-title">Video url:</div>
                  <div class="art-info-content" data-video="currentSrc"></div>
                </div>
                <div class="art-info-item">
                  <div class="art-info-title">Video volume:</div>
                  <div class="art-info-content" data-video="volume"></div>
                </div>
                <div class="art-info-item">
                  <div class="art-info-title">Video time:</div>
                  <div class="art-info-content" data-video="currentTime"></div>
                </div>
                <div class="art-info-item">
                  <div class="art-info-title">Video duration:</div>
                  <div class="art-info-content" data-video="duration"></div>
                </div>
                <div class="art-info-item">
                  <div class="art-info-title">Video resolution:</div>
                  <div class="art-info-content">
                    <span data-video="videoWidth"></span> x <span data-video="videoHeight"></span>
                  </div>
                </div>
              </div>
              <div class="art-info-close">[x]</div>
            </div>
            <div class="art-contextmenus"></div>
          </div>
        `}query(e){return z(e,this.$container)}init(){let{option:t}=this.art;if(t.useSSR||(this.$container.innerHTML=e.html),this.$player=this.query(`.art-video-player`),this.$video=this.query(`.art-video`),this.$track=this.query(`track`),this.$poster=this.query(`.art-poster`),this.$subtitle=this.query(`.art-subtitle`),this.$danmuku=this.query(`.art-danmuku`),this.$bottom=this.query(`.art-bottom`),this.$progress=this.query(`.art-progress`),this.$controls=this.query(`.art-controls`),this.$controlsLeft=this.query(`.art-controls-left`),this.$controlsCenter=this.query(`.art-controls-center`),this.$controlsRight=this.query(`.art-controls-right`),this.$layer=this.query(`.art-layers`),this.$loading=this.query(`.art-loading`),this.$notice=this.query(`.art-notice`),this.$noticeInner=this.query(`.art-notice-inner`),this.$mask=this.query(`.art-mask`),this.$state=this.query(`.art-state`),this.$setting=this.query(`.art-settings`),this.$info=this.query(`.art-info`),this.$infoPanel=this.query(`.art-info-panel`),this.$infoClose=this.query(`.art-info-close`),this.$contextmenu=this.query(`.art-contextmenus`),t.proxy){let e=t.proxy.call(this.art,this.art);X(e instanceof HTMLVideoElement||e instanceof HTMLCanvasElement,`Function 'option.proxy' needs to return 'HTMLVideoElement' or 'HTMLCanvasElement'`),Tt(e,this.$video),e.className=`art-video`,this.$video=e}t.backdrop&&B(this.$player,`art-backdrop`),T&&B(this.$player,`art-mobile`)}destroy(e){e?this.$container.innerHTML=``:B(this.$player,`art-destroy`)}},mt=class{on(e,t,n){let r=this.e||(this.e={});return(r[e]||(r[e]=[])).push({fn:t,ctx:n}),this}once(e,t,n){let r=this;function i(...a){r.off(e,i),t.apply(n,a)}return i._=t,this.on(e,i,n)}emit(e,...t){let n=((this.e||(this.e={}))[e]||[]).slice();for(let e=0;e<n.length;e+=1)n[e].fn.apply(n[e].ctx,t);return this}off(e,t){let n=this.e||(this.e={}),r=n[e],i=[];if(r&&t)for(let e=0,n=r.length;e<n;e+=1)r[e].fn!==t&&r[e].fn._!==t&&i.push(r[e]);return i.length?n[e]=i:delete n[e],this}},ht=0,L=[],e(`i`,R=class e extends mt{constructor(t,n){if(super(),!E)throw Error(`Artplayer can only be used in the browser environment`);this.id=++ht;let r=Rt(e.option,t);if(r.container=t.container,this.option=y(r,ye),this.isLock=!1,this.isReady=!1,this.isFocus=!1,this.isInput=!1,this.isRotate=!1,this.isDestroy=!1,this.template=new pt(this),this.events=new Se(this),this.storage=new ut(this),this.icons=new $e(this),this.i18n=new Te(this),this.notice=new it(this),this.player=new st(this),this.layers=new tt(this),this.controls=new xe(this),this.contextmenu=new be(this),this.subtitle=new ft(this),this.info=new et(this),this.loading=new nt(this),this.hotkey=new Ce(this),this.mask=new rt(this),this.setting=new lt(this),this.plugins=new ct(this),typeof n==`function`&&this.on(`ready`,()=>n.call(this,this)),e.DEBUG){let t=e=>console.log(`[ART.${this.id}] -> ${e}`);t(`Version@${e.version}`);for(let e=0;e<x.events.length;e++)this.on(`video:${x.events[e]}`,e=>t(`Event@${e.type}`))}L.push(this)}static get instances(){return L}static get version(){return b}static get config(){return x}static get utils(){return _e}static get scheme(){return ye}static get Emitter(){return mt}static get validator(){return y}static get kindOf(){return y.kindOf}static get html(){return pt.html}static get option(){return{id:``,container:`#artplayer`,url:``,poster:``,type:``,theme:`#f00`,volume:.7,isLive:!1,muted:!1,autoplay:!1,autoSize:!1,autoMini:!1,loop:!1,flip:!1,playbackRate:!1,aspectRatio:!1,screenshot:!1,setting:!1,hotkey:!0,pip:!1,mutex:!0,backdrop:!0,fullscreen:!1,fullscreenWeb:!1,subtitleOffset:!1,miniProgressBar:!1,useSSR:!1,playsInline:!0,lock:!1,gesture:!0,fastForward:!1,autoPlayback:!1,autoOrientation:!1,airplay:!1,proxy:void 0,layers:[],contextmenu:[],controls:[],settings:[],quality:[],highlight:[],plugins:[],thumbnails:{url:``,number:60,column:10,width:0,height:0,scale:1},subtitle:{url:``,type:``,style:{},name:``,escape:!0,encoding:`utf-8`,onVttLoad:e=>e},moreVideoAttr:{controls:!1,preload:C?`auto`:`metadata`},i18n:{},icons:{},cssVar:{},customType:{},lang:navigator?.language.toLowerCase()}}get proxy(){return this.events.proxy}get query(){return this.template.query}get video(){return this.template.$video}reset(){this.video.removeAttribute(`src`),this.video.load()}destroy(t=!0){e.REMOVE_SRC_WHEN_DESTROY&&this.reset(),this.events.destroy(),this.template.destroy(t),L.splice(L.indexOf(this),1),this.isDestroy=!0,this.emit(`destroy`)}}),R.STYLE=dt,R.DEBUG=!1,R.CONTEXTMENU=!0,R.NOTICE_TIME=2e3,R.SETTING_WIDTH=250,R.SETTING_ITEM_WIDTH=200,R.SETTING_ITEM_HEIGHT=35,R.RESIZE_TIME=200,R.SCROLL_TIME=200,R.SCROLL_GAP=50,R.AUTO_PLAYBACK_MAX=10,R.AUTO_PLAYBACK_MIN=5,R.AUTO_PLAYBACK_TIMEOUT=3e3,R.RECONNECT_TIME_MAX=5,R.RECONNECT_SLEEP_TIME=1e3,R.CONTROL_HIDE_TIME=3e3,R.DBCLICK_TIME=300,R.DBCLICK_FULLSCREEN=!0,R.MOBILE_DBCLICK_PLAY=!0,R.MOBILE_CLICK_PLAY=!1,R.AUTO_ORIENTATION_TIME=200,R.INFO_LOOP_TIME=1e3,R.FAST_FORWARD_VALUE=3,R.FAST_FORWARD_TIME=1e3,R.TOUCH_MOVE_RATIO=.5,R.VOLUME_STEP=.1,R.SEEK_STEP=5,R.PLAYBACK_RATE=[.5,.75,1,1.25,1.5,2],R.ASPECT_RATIO=[`default`,`4:3`,`16:9`],R.FLIP=[`normal`,`horizontal`,`vertical`],R.FULLSCREEN_WEB_IN_BODY=!0,R.LOG_VERSION=!0,R.USE_RAF=!1,R.REMOVE_SRC_WHEN_DESTROY=!0,E&&(Dt(`artplayer-style`,dt),setTimeout(()=>{R.LOG_VERSION&&console.log(`%c ArtPlayer %c ${R.version} %c https://artplayer.org`,`color: #fff; background: #5f5f5f`,`color: #fff; background: #4bc729`,``)},100)),R.PLAYBACK_RATE=[.5,.75,1,1.25,1.5,2,3,4],R.REMOVE_SRC_WHEN_DESTROY=!0,e(`r`,gt=[{icon:`iina`,name:`IINA`,scheme:`iina://weblink?url=$edurl`,platforms:[`MacOS`]},{icon:`potplayer`,name:`PotPlayer`,scheme:`potplayer://$durl`,platforms:[`Windows`]},{icon:`vlc`,name:`VLC`,scheme:`vlc://$durl`,platforms:[`Windows`,`MacOS`,`Linux`,`Android`,`iOS`]},{icon:`android`,name:`Android`,scheme:`intent:$durl#Intent;type=video/*;S.title=$name;end`,platforms:[`Android`]},{icon:`nplayer`,name:`nPlayer`,scheme:`nplayer-$durl`,platforms:[`Android`,`iOS`]},{icon:`omniplayer`,name:`OmniPlayer`,scheme:`omniplayer://weblink?url=$durl`,platforms:[`MacOS`]},{icon:`figplayer`,name:`Fig Player`,scheme:`figplayer://weblink?url=$durl`,platforms:[`Windows`,`MacOS`]},{icon:`vivid`,name:`Vivid Player`,scheme:`vividplayer://play?src=direct&u=$edurl&title=$name`,platforms:[`Windows`]},{icon:`infuse`,name:`Infuse`,scheme:`infuse://x-callback-url/play?url=$durl`,platforms:[`MacOS`,`iOS`]},{icon:`fileball`,name:`Fileball`,scheme:`filebox://play?url=$durl`,platforms:[`MacOS`,`iOS`]},{icon:`mxplayer`,name:`MX Player`,scheme:`intent:$durl#Intent;package=com.mxtech.videoplayer.ad;S.title=$name;end`,platforms:[`Android`]},{icon:`mxplayer-pro`,name:`MX Player Pro`,scheme:`intent:$durl#Intent;package=com.mxtech.videoplayer.pro;S.title=$name;end`,platforms:[`Android`]},{icon:`iPlay`,name:`iPlay`,scheme:`iplay://play/any?type=url&url=$bdurl`,platforms:[`iOS`]},{icon:`mpv`,name:`mpv`,scheme:`mpv://$edurl`,platforms:[`Windows`,`MacOS`,`Linux`,`Android`]}]),e(`t`,e=>{let{$container:t,$video:n}=e.template,r=t.parentElement;e.on(`ready`,()=>{r.style.maxHeight=`calc(100vh - ${r.offsetTop}px - 1.75rem)`,r.style.minHeight=`320px`,e.autoHeight()}),e.on(`resize`,()=>{e.autoHeight()}),e.on(`error`,()=>{n.style.height||(t.style.height=`60vh`,n.style.height=`100%`)})}),e(`n`,e=>{let{replace:t,pathname:v}=ie(),{currentObjLink:fe}=s(),{handleFolder:pe}=i(),[y,b]=ee(``),x=se(()=>{let e=!0,t=!1,n=d.objs.filter(n=>n.type===de.VIDEO?(n.name===d.obj.name?(e=!1,t=!0,b(n.name)):t=!1,!0):!1);if(t&&(e=ne().type!==`all`),e){let e=v();if(!e.endsWith(d.obj.name))return n.push(d.obj),b(d.obj.name),n;let t=d.objs.length>0;pe(oe(e),ae()+ +!!t,void 0,t,!1,!0)}return n}),S=u(),C=localStorage.getItem(`video_auto_next`);C||(C=`true`),e.onAutoNextChange(C===`true`);let[w,me]=ee(localStorage.getItem(`video_show_all_players`)===`true`),T=re(),E=se(()=>w()||T===`Unknown`?gt:gt.filter(e=>e.platforms.includes(T)));return _(h,{w:`$full`,spacing:`$2`,get children(){return[l(()=>e.children),_(c,{get when(){return y()!==``},get children(){return _(r,{spacing:`$2`,w:`$full`,get children(){return[_(le,{onChange:e=>{t(e)},get value(){return y()},get options(){return x().map(e=>({value:e.name}))}}),_(n,{css:{whiteSpace:`nowrap`},defaultChecked:C===`true`,onChange:t=>{e.onAutoNextChange(t.currentTarget.checked),localStorage.setItem(`video_auto_next`,t.currentTarget.checked.toString())},get children(){return S(`home.preview.auto_next`)}})]}})}}),_(ce,{wrap:`wrap`,gap:`$1`,justifyContent:`center`,alignItems:`center`,get children(){return[_(f,{get each(){return E()},children:e=>_(o,{placement:`top`,withArrow:!0,get label(){return e.name},get children(){return _(te,{get href(){return p(e.scheme,{raw_url:d.raw_url,name:d.obj.name,d_url:fe(!0)})},get children(){return _(m,{m:`0 auto`,boxSize:`$8`,get src(){return`${window.__dynamic_base__}/images/${e.icon}.webp`}})}})}})}),_(g,{"aria-label":`Show all players`,variant:`ghost`,onClick:()=>{let e=!w();me(e),localStorage.setItem(`video_show_all_players`,e.toString())},get icon(){return _(a,{as:ue,boxSize:`$6`,color:`accent.500`,get transform(){return w()?`rotate(180deg)`:`none`},transition:`transform 0.2s`})}})]}})]}})})}}})})();