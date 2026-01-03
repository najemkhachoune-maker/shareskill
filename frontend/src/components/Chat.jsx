import React, { useState, useEffect, useRef } from 'react';
import { Send, User, MoreVertical, PlusCircle } from 'lucide-react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import axios from 'axios';
import './Chat.css';

const API_BASE = '';

const Chat = ({ user, initialRecipient }) => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [conversations, setConversations] = useState([]); // List of users we chatted with
    const [activeRecipient, setActiveRecipient] = useState(null); // { id, username }
    const [recipientIdInput, setRecipientIdInput] = useState(''); // For manual testing
    const [isConnected, setIsConnected] = useState(false);

    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);

    useEffect(() => {
        if (initialRecipient) {
            // Priority to userId (Auth UUID) if available, otherwise fallback to id (Profile ID)
            const targetId = initialRecipient.userId || initialRecipient.id;
            if (targetId) {
                setActiveRecipient({
                    id: targetId.toString(),
                    username: initialRecipient.username || `User ${targetId}`
                });
            }
        }
    }, [initialRecipient]);

    useEffect(() => {
        if (user && user.id) {
            connect();
        }
        return () => {
            disconnect();
        };
    }, [user]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    // Load conversation history when active recipient changes
    useEffect(() => {
        if (activeRecipient && user.id) {
            fetchChatHistory(activeRecipient.id);
        }
    }, [activeRecipient, user]);

    const connect = () => {
        const socket = new SockJS(`${API_BASE}/ws`);
        const stompClient = Stomp.over(socket);
        stompClient.debug = null; // Disable debug logs
        const token = localStorage.getItem('token');

        stompClient.connect({ 'Authorization': `Bearer ${token}` }, (frame) => {
            console.log('Connected: ' + frame);
            setIsConnected(true);
            stompClientRef.current = stompClient;

            // Subscribe to my private messages
            // Backend sends via convertAndSendToUser(userId, "/queue/messages", ...)
            // Client should subscribe to /user/queue/messages. Spring automatically maps this to the user's session.
            stompClient.subscribe(`/user/queue/messages`, (notification) => {
                onMessageReceived(JSON.parse(notification.body));
            });

            // DEBUG: Subscribe to public topic
            stompClient.subscribe('/topic/public', (payload) => {
                const msg = JSON.parse(payload.body);
                console.log("Public Message Received:", msg);

                // Only display if I am the recipient OR the sender (to confirm sent)
                // Also verify user.id matches the recipientId (UUID check)
                const isForMe = msg.recipientId === user.id;
                const isFromMe = msg.senderId === user.id;

                console.log(`Filtering msg: MyID=${user.id}, MsgRecipient=${msg.recipientId}, MsgSender=${msg.senderId} => ForMe? ${isForMe}, FromMe? ${isFromMe}`);

                if (isForMe || isFromMe) {
                    onMessageReceived(msg);
                }
            });
        }, (error) => {
            console.error('Connection error:', error);
            setIsConnected(false);
            // Retry connection after 5s
            setTimeout(connect, 5000);
        });
    };

    const disconnect = () => {
        if (stompClientRef.current) {
            stompClientRef.current.disconnect();
        }
        setIsConnected(false);
    };

    const onMessageReceived = (payload) => {
        console.log("Message Received:", payload);
        // Payload is ChatNotification (id, senderId, senderName) or ChatMessage
        // We need to fetch the full message or just display it.
        // The backend ChatController sends ChatNotification which has content.

        const message = {
            id: payload.id,
            senderId: payload.senderId,
            recipientId: payload.recipientId, // Should be me
            content: payload.content || payload.text, // Adjust based on notification structure
            timestamp: new Date().toISOString() // Payload might not have time
        };

        // If this message is from the currently active recipient, add it
        // Or if it's from me (shouldn't happen via WS usually unless echoed)

        setMessages((prev) => [...prev, message]);

        // Also update conversations list if it's a new sender
        // For now, simpler handling:
        if (activeRecipient && payload.senderId === activeRecipient.id) {
            // Already viewing
        } else {
            // Notification logic or badge could go here
        }
    };

    const sendMessage = (e) => {
        e.preventDefault();
        if (!newMessage.trim() || !activeRecipient) return;

        if (!stompClientRef.current) {
            console.error("No WS connection");
            return;
        }

        const chatMessage = {
            senderId: user.id,
            recipientId: activeRecipient.id,
            senderName: user.username,
            recipientName: activeRecipient.username,
            content: newMessage,
            timestamp: new Date()
        };

        // Send to /app/chat
        stompClientRef.current.send("/app/chat", {}, JSON.stringify(chatMessage));

        // Optimistically add to UI
        const uiMessage = {
            ...chatMessage,
            id: Date.now().toString(), // Temp ID
        };
        setMessages((prev) => [...prev, uiMessage]);
        setNewMessage('');
    };

    const fetchChatHistory = async (recipientId) => {
        try {
            const token = localStorage.getItem('token');
            // Endpoint: /messages/{senderId}/{recipientId}
            // But controller is @GetMapping("/messages/...") - Wait, check controller mapping again.
            // Controller has @GetMapping("/messages/{senderId}/{recipientId}") but NO class level RequestMapping.
            // So it is root /messages...
            // Gateway routes /chat/** to chat-service. 
            // We need to call via Gateway. 
            // We'll update gateway to include this path or rely on relative proxy.
            const response = await axios.get(`${API_BASE}/messages/${user.id}/${recipientId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setMessages(response.data);
        } catch (err) {
            console.error("Failed to fetch history", err);
        }
    };

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    const handleManualRecipient = () => {
        if (!recipientIdInput) return;
        setActiveRecipient({ id: recipientIdInput, username: `User ${recipientIdInput.substr(0, 4)}` });
        setRecipientIdInput('');
    };

    return (
        <div className="chat-interface card glass">
            <div className="chat-sidebar">
                <div className="chat-search">
                    <p className="status-text">{isConnected ? '🟢 Connected' : '🔴 Disconnected'}</p>
                    <div className="manual-connect">
                        <input
                            type="text"
                            placeholder="Enter User ID to chat..."
                            value={recipientIdInput}
                            onChange={(e) => setRecipientIdInput(e.target.value)}
                        />
                        <button className="btn-icon-small" onClick={handleManualRecipient}><PlusCircle size={16} /></button>
                    </div>
                </div>
                <div className="conversations-list">
                    {/* Mock Conversations or Recent List */}
                    <div className="conversation-item" onClick={() => setActiveRecipient(null)}>
                        <div className="conv-info">
                            <h4>Select a user to chat</h4>
                        </div>
                    </div>
                    {activeRecipient && (
                        <div className="conversation-item active">
                            <div className="avatar">{activeRecipient.username.substring(0, 1)}</div>
                            <div className="conv-info">
                                <h4>{activeRecipient.username}</h4>
                                <p className="text-muted">{activeRecipient.id}</p>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            <div className="chat-main">
                {activeRecipient ? (
                    <>
                        <div className="chat-header">
                            <div className="avatar">{activeRecipient.username.substring(0, 1)}</div>
                            <div className="header-info">
                                <h3>{activeRecipient.username}</h3>
                                <span className="status-indicator online">Online or Offline</span>
                            </div>
                            <button className="icon-btn"><MoreVertical size={20} /></button>
                        </div>

                        <div className="messages-area">
                            {messages.map((msg, idx) => (
                                <div key={idx} className={`message ${msg.senderId === user.id ? 'sent' : 'received'}`}>
                                    <div className="message-content">
                                        <p>{msg.content}</p>
                                        {/* <span className="msg-time">{msg.timestamp}</span> */}
                                    </div>
                                </div>
                            ))}
                            <div ref={messagesEndRef} />
                        </div>

                        <form className="chat-input-area" onSubmit={sendMessage}>
                            <input
                                type="text"
                                placeholder="Type a message..."
                                value={newMessage}
                                onChange={(e) => setNewMessage(e.target.value)}
                            />
                            <button type="submit" className="btn btn-primary btn-icon">
                                <Send size={20} />
                            </button>
                        </form>
                    </>
                ) : (
                    <div className="chat-placeholder">
                        <h3>Select or add a user to start chatting</h3>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Chat;
