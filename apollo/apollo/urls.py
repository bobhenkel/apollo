"""apollo URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.8/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Add an import:  from blog import urls as blog_urls
    2. Add a URL to urlpatterns:  url(r'^blog/', include(blog_urls))
"""
from django.conf.urls import include, url
from django.contrib import admin
from rest_framework import routers
from restapi import views
from rest_framework.authtoken import views as auth_views

router = routers.DefaultRouter()
router.register(r'user', views.UserViewSet)
router.register(r'service', views.ServiceViewSet)
router.register(r'health-check', views.HealthCheckViewSet)
router.register(r'label', views.LabelViewSet)
router.register(r'constraints', views.ConstraintViewSet)
router.register(r'environment-variable', views.EnvironmentVariableViewSet)
router.register(r'volume', views.VolumeViewSet)
router.register(r'port-mapping', views.PortMappingViewSet)
router.register(r'environment', views.EnvironmentViewSet)
router.register(r'group', views.GroupViewSet)
router.register(r'permission', views.PermissionViewSet)
router.register(r'blocker', views.BlockerViewSet)
router.register(r'notification', views.NotificationViewSet)
router.register(r'watcher', views.WatcherViewSet)
router.register(r'deployable-version', views.DeployableVersionViewSet)
router.register(r'running-deployments', views.RunningDeploymentsViewSet)


urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),
    url(r'^login/', auth_views.obtain_auth_token),
    url(r'latest-deployments/', views.CurrentDeploymentsView.as_view()),
    url(r'deployment/[0-9]/logs', views.DeploymentViewSet.as_view({'get': 'logs'})),
    url(r'deployment/', views.DeploymentViewSet.as_view({'get': 'list', 'post': 'create'})),
    url(r'signup/', views.SignUpView.as_view()),
]
