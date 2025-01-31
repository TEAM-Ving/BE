"""
URL configuration for ving project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/5.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path,include
from django.conf import settings
from django.conf.urls.static import static
from camera_app import routing 
urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/home/', include('home.urls')),
    path('api/hls/', include('hls.urls')),
    path('api/camera_app/', include('camera_app.urls')),
    path('api/statistic/', include('statistic.urls')),
    path('api/ws/', include(routing.websocket_urlpatterns)),
    path('api/media_pipeline/', include('media_pipeline.urls')),
    path('api/network_throughput/', include('network_throughput.urls')),
    
]
if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
urlpatterns += static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)