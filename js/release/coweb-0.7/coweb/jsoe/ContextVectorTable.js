define(["coweb/jsoe/ContextVector"],function(a){var b=function(a,b){this.cvt=[],this.growTo(b+1),this.cvt[b]=a};b.prototype.toString=function(){var a=[];for(var b=0,c=this.cvt.length;b++;b<c){var d=this.cvt[b];a[b]=d.toString()}return a.toString()},b.prototype.getEquivalents=function(a,b){var c=[];for(var d=0,e=this.cvt.length;d<e;d++)d!==b&&this.cvt[d]===a&&c.push(d);return c},b.prototype.getState=function(){var a=[];for(var b=0,c=this.cvt.length;b<c;b++)a[b]=this.cvt[b].getState();return a},b.prototype.setState=function(b){this.cvt=[];for(var c=0,d=b.length;c<d;c++)this.cvt[c]=new a({state:b[c]})},b.prototype.growTo=function(b){for(var c=0,d=this.cvt.length;c<d;c++)this.cvt[c].growTo(b);for(c=this.cvt.length;c<b;c++){var e=new a({count:b});this.cvt.push(e)}},b.prototype.getContextVector=function(a){this.cvt.length<=a&&this.growTo(a+1);return this.cvt[a]},b.prototype.updateWithContextVector=function(a,b){this.cvt.length<=a&&this.growTo(a+1),b.getSize()<=a&&b.growTo(a+1),this.cvt[a]=b},b.prototype.updateWithOperation=function(a){var b=a.contextVector.copy();b.setSeqForSite(a.siteId,a.seqId),this.updateWithContextVector(a.siteId,b)},b.prototype.getMinimumContextVector=function(){if(!this.cvt.length)return null;var a=this.cvt[0].copy();for(var b=1,c=this.cvt.length;b<c;b++){var d=this.cvt[b];for(var e=0;e<c;e++){var f=d.getSeqForSite(e),g=a.getSeqForSite(e);f<g&&a.setSeqForSite(e,f)}}return a};return b})