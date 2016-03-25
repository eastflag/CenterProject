$(function() {
    $('#side-menu').metisMenu();
});

var app = angular.module('app', [
    'ngRoute', 'ui.bootstrap', 'ngCookies', 'toaster', 'ngAnimate', 'angular-jwt', 'ngAnimate'
]);

app.run(['$rootScope', '$cookieStore', '$http', 'toaster', function($rootScope, $cookieStore, $http, toaster) {
  	$rootScope.role_id = 0;
  	$rootScope.login_url = "/index.html";

  	$rootScope.refreshToken = function(new_token) {
		$rootScope.auth_token = new_token;

		var auth_info = {auth_token : $rootScope.auth_token, role_id : $rootScope.role_id};

		$cookieStore.put("auth_info", auth_info);

		$http.defaults.headers.post['X-Auth'] = $rootScope.auth_token;
  	};

  	$rootScope.logout = function() {
  		$rootScope.auth_token = null;
  		$cookieStore.remove("auth_info");
  		$rootScope.pop('error', 'LogOut', '세션이 만료되었습니다.', 3000);
  	}

  	$rootScope.pop = function(type, title, content, time) {
  		toaster.pop(type, title, content, time);
  	}

  	$rootScope.clear = function() {
  		toaster.clear();
  	}
}]);

app.config( ['$routeProvider', '$locationProvider', '$httpProvider', function ($routeProvider, $locationProvider, $httpProvider) {
	$routeProvider
	.when('/admin', {templateUrl: '/admin/templates/admin.html'})
	.when('/admin_access', {templateUrl: '/admin/templates/admin_access.html'})
	
	$locationProvider.html5Mode(false);
	$locationProvider.hashPrefix('!');

	$httpProvider.defaults.headers.post['X-Auth'] = "";
}]);

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
	this.getManagerList = function(admin) {
		return $http.post('/admin/api/getManagerList', admin);
	}
	this.addManager = function(admin) {
		return $http.post('/admin/api/addManager', admin);
	}
	this.modifyManager = function(admin) {
		return $http.post('/admin/api/modifyManager', admin);
	}
	this.removeManager = function(admin) {
		return $http.post('/admin/api/removeManager', admin);
	}
});

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

app.controller('MainCtrl', ['$scope', '$http', '$rootScope', '$cookieStore', 'MainSvc', 'jwtHelper', function ($scope, $http, $rootScope, $cookieStore, MainSvc, jwtHelper) {
	$scope.token = null;
	$scope.error = null;
	$scope.role_id = null;

	$scope.login = function() {
		$scope.error = null;
		MainSvc.getLogin({id:$scope.id, password:$scope.password})
		.success(function(value){
			if(value.result == 0) {
				$scope.id = null;
				$scope.pass = null;

				$scope.token = value.data.token;
				$scope.role_id = value.data.role_id;

				$rootScope.auth_token = $scope.token;
				$rootScope.role_id = $scope.role_id;

				var auth_info = {auth_token : $rootScope.auth_token, role_id : $rootScope.role_id};

				$cookieStore.put("auth_info", auth_info);

				$http.defaults.headers.post['X-Auth'] = $rootScope.auth_token;
				console.log('rootScope token:' + $rootScope.auth_token);
			} else {
				alert(value.msg);
			}
		})
		.error(function(error) {
			$scope.error = error;
		})
	}

	$scope.loggedIn = function() {
		if ($cookieStore.get("auth_info") != null && $cookieStore.get("auth_info") != undefined) {
			var auth_info = $cookieStore.get("auth_info");
			//expire time 검증
			var token = auth_info.auth_token;
			var isExpired = jwtHelper.isTokenExpired(token);
			
			if(isExpired) {
				return false;
			} else {
				$rootScope.auth_token = auth_info.auth_token;
				$rootScope.role_id = auth_info.role_id;
				$scope.role_id = auth_info.role_id;

				$http.defaults.headers.post['X-Auth'] = $rootScope.auth_token;
				return true
			}
		} else {
			return false;
		}
    }

    $scope.logOut = function() {
    	$rootScope.auth_token = null;
		$rootScope.role_id = 0;
		$scope.role_id = 0;

		$http.defaults.headers.post['X-Auth'] = "";

		$cookieStore.remove("auth_info");
    }
}]);


app.controller('AdminAccessCtrl', ['$scope', '$rootScope', '$cookieStore', 'AdminAccessSvc', function ($scope, $rootScope, $cookieStore, AdminAccessSvc) {
	$scope.currentPage = 1;
	$scope.totalCount = 0;

	$scope.getAdminAccessList = function() {
		AdminAccessSvc.getAdminAccessList({start_index:($scope.currentPage - 1) * 10, page_size:10})
		.success(function(datas, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
			$scope.datas = datas.data;
			$scope.totalCount = datas.total;
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.getAdminAccessList();
}]);


app.controller('AdminCtrl', ['$scope', '$rootScope', '$window', '$cookieStore', 'AdminSvc', function ($scope, $rootScope, $window, $cookieStore, AdminSvc) {
	$scope.admins = [];
	
	$scope.currentPageAdmin = 1;
	$scope.totalAdminListCount = 0;

	$scope.admin_mode = "";
	$scope.admin_mode_text = "관리자 추가";

	$scope.roles = [
		{code: 1, name: "슈퍼관리자"},
		{code: 2, name: "상담사"},
		{code: 3, name: "측정관리자"}
	];


	$scope.getManagerList = function(){
		AdminSvc.getManagerList({start_index:($scope.currentPageAdmin - 1) * 10, page_size:10})
		.success(function(admins, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
			$scope.admins = admins.data;
			$scope.totalAdminListCount = admins.total;

			$scope.clearAdmin();
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.getManagerList();

	$scope.clearAdmin = function() {
		$scope.admin_mode = "";
		$scope.admin_mode_text = "관리자 추가";

		$scope.id = null;
		$scope.pass = null;
		$scope.name = null;
		$scope.role_id = "";
	}

	$scope.adminListPageChanged = function() {
		$scope.getManagerList();
	};

	$scope.editAdmin = function(admin) {
		$scope.manager_id = admin.manager_id;
		$scope.admin_mode = "edit";
		$scope.admin_mode_text = "관리자 수정";

		$scope.id = admin.id;
		$scope.pass = admin.pass;
		$scope.name = admin.name;
		$scope.role_id = admin.role_id;
	}

	$scope.addAdmin = function() {
		var admin = {
			id: $scope.id,
			pass: $scope.pass,
			name: $scope.name,
			role_id: $scope.role_id
		}

		AdminSvc.addManager(admin)
		.success(function(data){
			$scope.clearAdmin();
			$scope.getManagerList();
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.modifyAdmin = function() {
		var admin = {
			manager_id: $scope.manager_id,
			name: $scope.name,
			role_id: $scope.role_id
		}
		
		AdminSvc.modifyManager(admin)
		.success(function(data){
			$scope.clearAdmin();
			$scope.getManagerList();
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.deleteAdmin = function(admin) {
		if ($window.confirm("삭제하시겠습니까?")) {	
			AdminSvc.removeManager(admin)
			.success(function(result) {
				$scope.currentPageAdmin = 1;
				
				$scope.clearAdmin();
				$scope.getManagerList();
			}).error(function(data, status) {
				if (status == 401) {
					$rootScope.logout();
				} else {
					alert("error : " + data.message);
				}
			});
		};
	}
}]);