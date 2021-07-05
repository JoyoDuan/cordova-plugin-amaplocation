var exec = require('cordova/exec');

// 这里的命令AMapLocationPlugin与plugin.xml无关，用abc都可以，module.exports用于require('')
function AMapLocationPlugin() {}

AMapLocationPlugin.prototype.getLocation = function(successCallback, errorCallback, options) {
	exec(successCallback, errorCallback, "AMapLocationPlugin", "getLocation", [
		options.isOnceLocation,
		options.locationInterval
	]);
};

AMapLocationPlugin.prototype.stopLocation = function(successCallback, errorCallback) {
	exec(successCallback, errorCallback, "AMapLocationPlugin", "stopLocation", []);
};

AMapLocationPlugin.prototype.enableBackgroundLocation = function(successCallback, errorCallback) {
	exec(successCallback, errorCallback, "AMapLocationPlugin", "enableBackgroundLocation", []);
};

AMapLocationPlugin.prototype.disableBackgroundLocation = function(successCallback, errorCallback) {
	exec(successCallback, errorCallback, "AMapLocationPlugin", "disableBackgroundLocation", []);
};

module.exports = new AMapLocationPlugin();
