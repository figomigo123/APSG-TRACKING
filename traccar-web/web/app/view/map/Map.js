/*
 * Copyright 2015 - 2017 Anton Tananaev (anton@traccar.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

Ext.define('Traccar.view.map.Map', {
    extend: 'Traccar.view.map.BaseMap',
    xtype: 'mapView',

    requires: [
        'Traccar.view.map.MapController',
        'Traccar.view.SettingsMenu'
    ],

    controller: 'map',

    title: Strings.mapTitle,
    tbar: {
        componentCls: 'toolbar-header-style',
        defaults: {
            xtype: 'button',
            tooltipType: 'title',
            stateEvents: ['toggle'],
            enableToggle: true,
            stateful: {
                pressed: true
            }
        },
        items: [{
            xtype: 'tbtext',
            html: Strings.mapTitle,
            baseCls: 'x-panel-header-title-default'
        }, {
            xtype: 'tbfill'
        }, {
            handler: 'showReports',
            reference: 'showReportsButton',
            glyph: 'xf0f6@FontAwesome',
            stateful: false,
            enableToggle: false,
            tooltip: Strings.reportTitle
        }, {
            handler: 'showEvents',
            reference: 'showEventsButton',
            glyph: 'xf27b@FontAwesome',
            stateful: false,
            enableToggle: false,
            tooltip: Strings.reportEvents
        }, {
            handler: 'updateGeofences',
            reference: 'showGeofencesButton',
            glyph: 'xf21d@FontAwesome',
            pressed: true,
            stateId: 'show-geofences-button',
            tooltip: Strings.sharedGeofences
        }, {
            handler: 'showCurrentLocation',
            glyph: 'xf124@FontAwesome',
            tooltip: Strings.mapCurrentLocation
        }, {
            handler: 'showLiveRoutes',
            reference: 'showLiveRoutes',
            glyph: 'xf1b0@FontAwesome',
            stateId: 'show-live-routes-button',
            tooltip: Strings.mapLiveRoutes
        }, {
            reference: 'deviceFollowButton',
            glyph: 'xf05b@FontAwesome',
            tooltip: Strings.deviceFollow,
            stateId: 'device-follow-button',
            toggleHandler: 'onFollowClick'
        }, {
            xtype: 'settingsMenu',
            enableToggle: false
        }]
    },

    getMarkersSource: function () {        
        return this.markersSource;
        // console.log(this.clusters.values_);
        // return this.clusters.values_.source;
    },
    getClusterSource: function () {        
        return this.clusters;
        // console.log(this.clusters.values_);
        // return this.clusters.values_.source;
    },

    getAccuracySource: function () {
        return this.accuracySource;
    },

    getRouteSource: function () {
        return this.routeSource;
    },

    getGeofencesSource: function () {
        return this.geofencesSource;
    },

    getLiveRouteSource: function () {
        return this.liveRouteSource;
    },

    getLiveRouteLayer: function () {
        return this.liveRouteLayer;
    },

    initMap: function () {
        debugger
        this.callParent();

        this.geofencesSource = new ol.source.Vector({});
        this.map.addLayer(new ol.layer.Vector({
            name: 'geofencesLayer',
            source: this.geofencesSource
        }));

        this.liveRouteSource = new ol.source.Vector({});
        this.liveRouteLayer = new ol.layer.Vector({
            source: this.liveRouteSource,
            visible: this.lookupReference('showLiveRoutes').pressed
        });
        this.map.addLayer(this.liveRouteLayer);

        this.routeSource = new ol.source.Vector({});
        this.map.addLayer(new ol.layer.Vector({
            source: this.routeSource
        }));

        

        this.accuracySource = new ol.source.Vector({});
        this.map.addLayer(new ol.layer.Vector({
            name: 'accuracyLayer',
            source: this.accuracySource
        }));
        

        this.markersSource = new ol.source.Vector({
           
        });
        console.log("Feature Collections", this.markersSource.featuresCollection_);

        this.map.addLayer(new ol.layer.Vector({
            source: this.markersSource
        }));

        var clusterSource = new ol.source.Cluster({
            distance: 50,
            source: this.markersSource
        });

       var styleCache = [];
        this.clusters = new ol.layer.Vector({
            source: clusterSource,
            style: function (feature) {                
                var size = feature.get('features').length;
                var style = styleCache[size];
                if (!style) {
                    style = new ol.style.Style({
                        image: new ol.style.Circle({
                            radius: 10,
                            stroke: new ol.style.Stroke({
                                color: '#fff'
                            }),
                            fill: new ol.style.Fill({
                                color: '#3399CC'
                            })
                        }),
                        text: new ol.style.Text({
                            text: size.toString(),
                            fill: new ol.style.Fill({
                                color: '#fff'
                            })
                        })
                    });
                    styleCache[size] = style;
                }
                return style;
                // if (size > 1) {
                   
                // } else {
                //     debugger
                //     console.log("============================================", feature.get('features'));
                //     return feature.get('features')[0].getStyle();
                // }
            }
        });
        this.map.addLayer(this.clusters);
    }
});
