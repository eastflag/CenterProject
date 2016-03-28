$(function() {
    $('#side-menu').metisMenu();
});

var app = angular.module('app', [
    'ngRoute', 'ui.bootstrap', 'ngCookies', 'toaster', 'ngAnimate', 'angular-jwt', 'ngAnimate'
]);

app.config( ['$routeProvider', '$locationProvider', '$httpProvider', function ($routeProvider, $locationProvider, $httpProvider) {
	$routeProvider
		.when('/admin', {templateUrl: '/admin/partial/admin.html'})
		.when('/admin_access', {templateUrl: '/admin/partial/admin_access.html'})

	$locationProvider.html5Mode(false);
	$locationProvider.hashPrefix('!');

	$httpProvider.defaults.headers.post['X-Auth'] = "";
}]);

app.run(['$rootScope', '$cookieStore', '$http', 'toaster', function($rootScope, $cookieStore, $http, toaster) {

}]);

app.directive('ngEnter', function () {
	return function (scope, element, attrs) {
		element.bind("keydown keypress", function (event) {
			if(event.which === 13) {
				scope.$apply(function (){
					scope.$eval(attrs.ngEnter);
				});

				event.preventDefault();
			}
		});
	};
});

app.service('MainSvc', function($http) {
	this.getLogin = function(login) {
		return $http.post('/api/adminLogin', login);
	}
})

app.service('AdminAccessSvc', function($http) {
	this.getAdminAccessList = function(access) {
		return $http.post('/admin/api/getAdminAccessList', access);
	}
});

app.service('AdminSvc', function($http) {
	this.getAdminList = function(admin) {
		return $http.post('/admin/api/getAdminList', admin);
	}
	this.addAdmin = function(admin) {
		return $http.post('/admin/api/addAdmin', admin);
	}
	this.modifyAdmin = function(admin) {
		return $http.post('/admin/api/modifyAdmin', admin);
	}
	this.removeAdmin = function(admin) {
		return $http.post('/admin/api/removeAdmin', admin);
	}
});



app.controller('MainCtrl', ['$scope', '$http', '$rootScope', '$cookieStore', 'MainSvc', 'jwtHelper', 'toaster',
	function ($scope, $http, $rootScope, $cookieStore, MainSvc, jwtHelper, toaster) {
	$scope.token = null;
	$scope.error = null;
	$scope.role_level = null;

	$scope.login = function() {
		$scope.error = null;
		MainSvc.getLogin({id:$scope.id, password:$scope.password})
		.success(function(value){
			if(value.result == 0) {
				$scope.id = null;
				$scope.password = null;

				$scope.token = value.data.token;
				$scope.role_level = value.data.role_level;

				var auth_info = {token : $scope.token, role_level : $scope.role_level};

				$cookieStore.put("auth_info", auth_info);

				$http.defaults.headers.post['X-Auth'] = $scope.token;
				console.log('token:' + $scope.token);
			} else {
				alert(value.msg);
			}
		})
		.error(function(error) {
			$scope.error = error;
		})
	};

	$scope.loggedIn = function() {
		if ($cookieStore.get("auth_info") != null && $cookieStore.get("auth_info") != undefined) {
			var auth_info = $cookieStore.get("auth_info");
			//expire time 검증
			var isExpired = jwtHelper.isTokenExpired(auth_info.token);
			
			if(isExpired) {
				return false;
			} else {
				$scope.token = auth_info.token;
				$scope.role_level = auth_info.role_level;

				$http.defaults.headers.post['X-Auth'] = $scope.token;
				return true
			}
		} else {
			return false;
		}
    };

	$scope.refreshToken = function(new_token) {
		$scope.token = new_token;
		var auth_info = {token : $scope.token, role_level : $scope.role_level};
		$cookieStore.put("auth_info", auth_info);
		$http.defaults.headers.post['X-Auth'] = $scope.token;
	};

	//로그아웃 버튼
    $scope.logOut = function() {
    	$scope.token = null;
		$scope.role_level = null;

		$http.defaults.headers.post['X-Auth'] = "";
		$cookieStore.remove("auth_info");
    };

	//강제 로그아웃
	$scope.logout = function () {
		$scope.logOut();
		$scope.pop('error', 'LogOut', '세션이 만료되었습니다.', 2000);
	};

	$scope.pop = function(type, title, content, time) {
		toaster.pop(type, title, content, time);
	};

	$scope.clear = function() {
		toaster.clear();
	};
}]);


app.controller('AdminAccessCtrl', ['$scope', 'AdminAccessSvc', function ($scope, AdminAccessSvc) {
	$scope.currentPage = 1;
	$scope.totalCount = 0;

	$scope.getAdminAccessList = function() {
		console.log('adminAccessList:' + $scope.$parent.token);
		AdminAccessSvc.getAdminAccessList({start_index:($scope.currentPage - 1) * 10, page_size:10})
		.success(function(value, status, headers) {
			$scope.$parent.refreshToken(headers('X-Auth'));
			$scope.datas = value.data;
			$scope.totalCount = value.total;
		}).error(function(value, status) {
			if (status == 401) {
				$scope.$parent.logout();
			} else {
				alert("error : " + value.message);
			}
		});
	}

	$scope.getAdminAccessList();
}]);


app.controller('AdminCtrl', ['$scope', '$window', 'AdminSvc', function ($scope, $window, AdminSvc) {
	$scope.admins = [];
	
	$scope.currentPageAdmin = 1;
	$scope.totalAdminListCount = 0;

	$scope.admin_mode = "";
	$scope.admin_mode_text = "관리자 추가";

	$scope.roles = {"슈퍼관리자":1, "일반관리자":2};

	$scope.getAdminList = function(){
		AdminSvc.getAdminList({start_index:($scope.currentPageAdmin - 1) * 10, page_size:10})
		.success(function(value, status, headers) {
			$scope.$parent.refreshToken(headers('X-Auth'));
			$scope.admins = value.data;
			$scope.totalAdminListCount = value.total;

			$scope.clearAdmin();
		}).error(function(value, status) {
			if (status == 401) {
				$scope.$parent.logout();
			} else {
				alert("error : " + value.message);
			}
		});
	}

	$scope.getAdminList();

	$scope.clearAdmin = function() {
		$scope.admin_mode = "";
		$scope.admin_mode_text = "관리자 추가";

		$scope.id = null;
		$scope.password = null;
		$scope.name = null;
		$scope.role_level = "";
	}

	$scope.adminListPageChanged = function() {
		$scope.getAdminList();
	};

	$scope.editAdmin = function(admin) {
		$scope.admin_id = admin.admin_id;
		$scope.admin_mode = "edit";
		$scope.admin_mode_text = "관리자 수정";

		$scope.id = admin.id;
		$scope.password = admin.password;
		$scope.name = admin.name;
		$scope.role_level = admin.role_level;
	}

	$scope.addAdmin = function() {
		var admin = {
			id: $scope.id,
			password: $scope.password,
			name: $scope.name,
			role_level: $scope.role_level
		}

		AdminSvc.addAdmin(admin)
		.success(function(value){
			$scope.clearAdmin();
			$scope.getAdminList();
		}).error(function(value, status) {
			if (status == 401) {
				$scope.$parent.logout();
			} else {
				alert("error : " + value.message);
			}
		});
	}

	$scope.modifyAdmin = function() {
		var admin = {
			admin_id: $scope.admin_id,
			name: $scope.name,
			role_level: $scope.role_level
		}
		
		AdminSvc.modifyAdmin(admin)
		.success(function(value){
			$scope.clearAdmin();
			$scope.getAdminList();
		}).error(function(value, status) {
			if (status == 401) {
				$scope.$parent.logout();
			} else {
				alert("error : " + value.message);
			}
		});
	}

	$scope.deleteAdmin = function(admin) {
		if ($window.confirm("삭제하시겠습니까?")) {	
			AdminSvc.removeAdmin(admin)
			.success(function(result) {
				$scope.currentPageAdmin = 1;
				
				$scope.clearAdmin();
				$scope.getAdminList();
			}).error(function(data, status) {
				if (status == 401) {
					$scope.$parent.logout();
				} else {
					alert("error : " + data.message);
				}
			});
		};
	}
}]);