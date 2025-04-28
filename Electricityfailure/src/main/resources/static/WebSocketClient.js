// power-alert.js

class PowerAlertClient {
    constructor(baseUrl = 'ws://localhost:8080/ws') {
        this.baseUrl = baseUrl;
        this.socket = null;
        this.listeners = {
            'onConnect': [],
            'onDisconnect': [],
            'onFailure': [],
            'onResolution': [],
            'onError': []
        };
    }

    connect(token = null) {
        // Add token for authentication if needed
        let url = this.baseUrl;
        if (token) {
            url += '?token=' + token;
        }

        this.socket = new WebSocket(url);

        this.socket.onopen = () => {
            console.log('Connected to Power Alert WebSocket');
            this._notifyListeners('onConnect');
        };

        this.socket.onclose = () => {
            console.log('Disconnected from Power Alert WebSocket');
            this._notifyListeners('onDisconnect');

            // Auto reconnect after 5 seconds
            setTimeout(() => this.connect(token), 5000);
        };

        this.socket.onerror = (error) => {
            console.error('WebSocket Error:', error);
            this._notifyListeners('onError', error);
        };

        this.socket.onmessage = (event) => {
            const data = JSON.parse(event.data);
            console.log('Received event:', data);

            switch (data.type) {
                case 'FAILURE':
                    this._notifyListeners('onFailure', data.payload);
                    break;
                case 'RESOLUTION':
                    this._notifyListeners('onResolution', data.payload);
                    break;
                default:
                    console.log('Unknown event type:', data.type);
            }
        };
    }

    disconnect() {
        if (this.socket) {
            this.socket.close();
            this.socket = null;
        }
    }

    on(event, callback) {
        if (this.listeners[event]) {
            this.listeners[event].push(callback);
        }
        return this; // Allow chaining
    }

    off(event, callback) {
        if (this.listeners[event]) {
            this.listeners[event] = this.listeners[event].filter(cb => cb !== callback);
        }
        return this; // Allow chaining
    }

    _notifyListeners(event, data) {
        if (this.listeners[event]) {
            this.listeners[event].forEach(callback => callback(data));
        }
    }

    // Helper method to subscribe to specific areas
    subscribeToArea(areaCode) {
        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify({
                action: 'SUBSCRIBE',
                payload: { areaCode }
            }));
        } else {
            console.error('WebSocket not connected. Cannot subscribe.');
        }
    }

    // Helper method to unsubscribe from specific areas
    unsubscribeFromArea(areaCode) {
        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify({
                action: 'UNSUBSCRIBE',
                payload: { areaCode }
            }));
        } else {
            console.error('WebSocket not connected. Cannot unsubscribe.');
        }
    }
}

// Usage example:
// const powerAlert = new PowerAlertClient();
//
// powerAlert
//     .on('onConnect', () => {
//         console.log('Successfully connected!');
//         powerAlert.subscribeToArea('NYC-001');
//     })
//     .on('onFailure', (failure) => {
//         console.log('New failure detected:', failure);
//         showNotification(`Power outage reported in ${failure.areaName}`);
//     })
//     .on('onResolution', (resolution) => {
//         console.log('Failure resolved:', resolution);
//         showNotification(`Power restored in ${resolution.areaName}`);
//     });
//
// powerAlert.connect();