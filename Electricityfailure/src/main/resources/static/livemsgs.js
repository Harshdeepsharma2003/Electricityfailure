
  // Configuration
  const WEBSOCKET_URL = 'wss://mpeb-api.example.com/ws/kafka-monitor';
  let socket;
  let currentTopic = 'all';
  let messageCount = 0;
  let producersCount = 0;
  let consumersCount = 0;
  let messagesPerSecond = 0;
  let messageCountLastSecond = 0;
  let isPaused = false;
  let messageLimit = 100;

  // Initialize the connection
  function connectWebSocket() {
      // Close existing connection if any
      if (socket) {
          socket.close();
      }

      socket = new WebSocket(WEBSOCKET_URL);

      socket.onopen = function() {
          console.log('Connected to Kafka monitor');
          addSystemMessage('Connected to Kafka monitor server');

          // Subscribe to topics
          const subscribeMsg = {
              action: 'subscribe',
              topics: ['electricity-faults', 'maintenance-schedules', 'fault-updates', 'notifications']
          };
          socket.send(JSON.stringify(subscribeMsg));
      };

      socket.onmessage = function(event) {
          if (isPaused) return;

          const data = JSON.parse(event.data);

          // Update stats
          messageCount++;
          messageCountLastSecond++;
          document.getElementById('messages-count').textContent = messageCount;

          if (data.producerCount !== undefined) {
              producersCount = data.producerCount;
              document.getElementById('producers-count').textContent = producersCount;
          }

          if (data.consumerCount !== undefined) {
              consumersCount = data.consumerCount;
              document.getElementById('consumers-count').textContent = consumersCount;
          }

          // Apply filters
          const filterType = document.getElementById('filter-type').value;
          const filterLocation = document.getElementById('filter-location').value.toLowerCase();

          if (filterType !== 'all' && data.eventType !== filterType) {
              return;
          }

          if (filterLocation && data.content) {
              const contentStr = JSON.stringify(data.content).toLowerCase();
              if (!contentStr.includes(filterLocation)) {
                  return;
              }
          }

          // Handle different types of messages
          if (currentTopic === 'all' || data.topic === currentTopic) {
              addMessage(data);
          }

          // Enforce message limit
          enforceMessageLimit();
      };

      socket.onclose = function() {
          console.log('Disconnected from Kafka monitor');
          addSystemMessage('Disconnected from Kafka monitor server. Will attempt to reconnect in 5 seconds...');

          // Auto reconnect after 5 seconds
          setTimeout(connectWebSocket, 5000);
      };

      socket.onerror = function(error) {
          console.error('WebSocket error:', error);
          addSystemMessage('Error connecting to Kafka monitor server');
      };
  }

  // Add a message to the container
  function addMessage(data) {
      const messagesContainer = document.getElementById('messages-container');
      const messageElement = document.createElement('div');

      // Determine if producer or consumer message
      const isProducer = data.messageType === 'PRODUCER';
      messageElement.className = `message ${isProducer ? 'message-producer' : 'message-consumer'}`;

      // Create message time if timestamps are enabled
      const showTimestamps = document.getElementById('show-timestamps').checked;
      if (showTimestamps) {
          const timeElement = document.createElement('div');
          timeElement.className = 'message-time';
          timeElement.textContent = new Date().toLocaleTimeString();
          messageElement.appendChild(timeElement);
      }

      // Create message content
      const contentElement = document.createElement('div');
      contentElement.className = 'message-content';

      // Format JSON content
      const contentPre = document.createElement('pre');
      contentPre.textContent = JSON.stringify(data.content, null, 2);
      contentElement.appendChild(contentPre);
      messageElement.appendChild(contentElement);

      // Create message metadata
      const metaElement = document.createElement('div');
      metaElement.className = 'message-meta';

      const topicSpan = document.createElement('span');
      topicSpan.innerHTML = `Topic: <span class="badge badge-info">${data.topic}</span>`;
      metaElement.appendChild(topicSpan);

      const typeSpan = document.createElement('span');
      typeSpan.innerHTML = `Type: <span class="badge ${isProducer ? 'badge-primary' : 'badge-success'}">${isProducer ? 'Producer' : 'Consumer'}</span>`;
      metaElement.appendChild(typeSpan);

      const eventSpan = document.createElement('span');
      eventSpan.innerHTML = `Event: <span class="badge badge-warning">${data.eventType || 'N/A'}</span>`;
      metaElement.appendChild(eventSpan);

      messageElement.appendChild(metaElement);

      // Add to container
      messagesContainer.appendChild(messageElement);

      // Auto-scroll if enabled
      if (document.getElementById('auto-scroll').checked) {
          messagesContainer.scrollTop = messagesContainer.scrollHeight;
      }
  }

  // Add a system message
  function addSystemMessage(message) {
      const messagesContainer = document.getElementById('messages-container');
      const messageElement = document.createElement('div');
      messageElement.className = 'message';
      messageElement.style.backgroundColor = '#f8f9fa';
      messageElement.style.borderLeft = '4px solid #6c757d';
      messageElement.style.color = '#6c757d';
      messageElement.style.fontStyle = 'italic';
      messageElement.textContent = message;
      messagesContainer.appendChild(messageElement);

      // Auto-scroll if enabled
      if (document.getElementById('auto-scroll').checked) {
          messagesContainer.scrollTop = messagesContainer.scrollHeight;
      }
  }

  // Enforce message limit
  function enforceMessageLimit() {
      const messagesContainer = document.getElementById('messages-container');
      const messages = messagesContainer.querySelectorAll('.message');

      if (messages.length > messageLimit) {
          const toRemove = messages.length - messageLimit;
          for (let i = 0; i < toRemove; i++) {
              messagesContainer.removeChild(messages[i]);
          }
      }
  }

  // Calculate messages per second
  setInterval(function() {
      document.getElementById('messages-per-sec').textContent = messageCountLastSecond;
      messageCountLastSecond = 0;
  }, 1000);

  // Event listeners
  document.addEventListener('DOMContentLoaded', function() {
      // Connect to WebSocket
      connectWebSocket();

      // Topic selector
      document.querySelectorAll('.topic-btn').forEach(btn => {
          btn.addEventListener('click', function() {
              document.querySelectorAll('.topic-btn').forEach(b => b.classList.remove('active'));
              this.classList.add('active');
              currentTopic = this.dataset.topic;

              // Clear messages
              document.getElementById('messages-container').innerHTML = '';
              addSystemMessage(`Switched to topic: ${currentTopic}`);
          });
      });

      // Clear messages button
      document.getElementById('clear-messages').addEventListener('click', function() {
          document.getElementById('messages-container').innerHTML = '';
          addSystemMessage('Messages cleared');
      });

      // Reconnect button
      document.getElementById('reconnect').addEventListener('click', function() {
          addSystemMessage('Manually reconnecting...');
          connectWebSocket();
      });

      // Export logs button
      document.getElementById('export-logs').addEventListener('click', function() {
          const messagesContainer = document.getElementById('messages-container');
          const messages = messagesContainer.querySelectorAll('.message');

          let exportData = [];
          messages.forEach(msg => {
              const content = msg.querySelector('pre');
              if (content) {
                  try {
                      exportData.push(JSON.parse(content.textContent));
                  } catch (e) {
                      exportData.push({ text: content.textContent });
                  }
              } else {
                  exportData.push({ text: msg.textContent });
              }
          });

          const dataStr = JSON.stringify(exportData, null, 2);
          const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);

          const exportFileDefaultName = `kafka-monitor-export-${new Date().toISOString()}.json`;

          const linkElement = document.createElement('a');
          linkElement.setAttribute('href', dataUri);
          linkElement.setAttribute('download', exportFileDefaultName);
          linkElement.click();
      });

      // Pause stream toggle
      document.getElementById('pause-stream').addEventListener('change', function() {
          isPaused = this.checked;
          if (isPaused) {
              addSystemMessage('Message stream paused');
          } else {
              addSystemMessage('Message stream resumed');
          }
      });

      // Compact view toggle
      document.getElementById('compact-view').addEventListener('change', function() {
          const messagesContainer = document.getElementById('messages-container');
          if (this.checked) {
              messagesContainer.querySelectorAll('.message-content pre').forEach(pre => {
                  pre.style.maxHeight = '50px';
                  pre.style.overflow = 'hidden';
              });
          } else {
              messagesContainer.querySelectorAll('.message-content pre').forEach(pre => {
                  pre.style.maxHeight = 'none';
              });
          }
      });

      // Message limit select
      document.getElementById('message-limit').addEventListener('change', function() {
          messageLimit = parseInt(this.value);
          enforceMessageLimit();
      });
  });
