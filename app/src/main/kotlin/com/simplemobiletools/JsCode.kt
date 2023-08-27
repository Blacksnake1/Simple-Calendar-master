package com.simplemobiletools

const val listenerCodeDemo = "javascript:(" +
        "function() {" +
        "   window.addEventListener('login', function(e) {" +
        "       console.log('#login' + JSON.stringify(e.detail.key));" +
        "       window.jsbridge.onEventReceived('login','')" +
        "   });" +
        "   window.addEventListener('logout', function(e) {" +
        "       console.log('#logout' + JSON.stringify(e.detail));" +
        "       window.jsbridge.onEventReceived('logout','')" +
        "   });" +
        "   window.addEventListener('register', function(e) {" +
        "       console.log('#register' + JSON.stringify(e.detail));" +
        "       window.jsbridge.onEventReceived('register','')" +
        "   });" +
        "   window.addEventListener('charge', function(e) {" +
        "       console.log('#charge' + JSON.stringify(e.detail));" +
        "       window.jsbridge.onEventReceived('charge','')" +
        "   });" +
        "   window.addEventListener('chargeSuccess', function(e) {" +
        "       console.log('#chargeSuccess' + JSON.stringify(e.detail.amount));" +
        "       window.jsbridge.onEventReceived('chargeSuccess',e.detail.amount)" +
        "   });" +
        "})()"


const val setPhilippinesTimeZone = ""
