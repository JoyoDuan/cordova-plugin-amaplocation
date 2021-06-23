var exec = require('cordova/exec');

// 这里的命令AMapLocationPlugin与plugin.xml无关，用abc都可以，module.exports用于require('')
function AMapLocationPlugin() {}

AMapLocationPlugin.prototype.getLocation = function(successCallback, errorCallback, locationOptions) {
	exec(successCallback, errorCallback, "AMapLocationPlugin", "getLocation", [
		locationOptions.isOnceLocation,
		locationOptions,interval
	]);
};

module.exports = new AMapLocationPlugin();
