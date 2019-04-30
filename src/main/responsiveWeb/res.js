function() {
  // deicePixelRatio ：设备像素
  var scale = 1 / devicePixelRatio;
  //设置meta 压缩界面 模拟设备的高分辨率
  document.querySelector('meta[name="viewport"]').setAttribute('content', 'initial-scale=' + scale + ', maximum-scale=' + scale + ', minimum-scale=' + scale + ', user-scalable=no');
  //debounce 为节流函数
  var reSize = _.debounce(function() {
      var deviceWidth = document.documentElement.clientWidth > 1300 ? 1300 : document.documentElement.clientWidth;
      //字体缩放比例值 6.4
      document.documentElement.style.fontSize = (deviceWidth / 6.4) + 'px';
  }, 50);
 
  window.onresize = reSize;

