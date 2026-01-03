import React, { useState } from 'react';
import { ShieldCheck, Mail, Lock, LogIn, UserPlus } from 'lucide-react';
import axios from 'axios';
import './Login.css';

const Login = ({ onLoginSuccess }) => {
    const [isLogin, setIsLogin] = useState(true);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [username, setUsername] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            if (isLogin) {
                const response = await axios.post('/api/auth/login', {
                    email,
                    password
                });

                const { accessToken, userId } = response.data;
                localStorage.setItem('token', accessToken);
                // On simule un objet user avec l'email et l'id pour l'instant
                onLoginSuccess({ email, id: userId, username: email.split('@')[0] });
            } else {
                console.log('Attempting registration with:', { email, username, firstName, lastName });
                await axios.post('/api/auth/register', {
                    email,
                    username,
                    password,
                    firstName,
                    lastName
                });

                console.log('Registration success');
                setIsLogin(true);
                setError('Registration successful! Please login.');
            }
        } catch (err) {
            console.error('Registration/Login error:', err.response?.data || err.message);
            const detailedError = err.response?.data?.message || err.response?.data?.error || err.message;
            setError(detailedError || 'An error occurred. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-page">
            <div className="login-card glass">
                <div className="login-header">
                    <div className="logo">
                        <ShieldCheck size={40} color="var(--primary)" />
                        <h1 className="gradient-text">SkillVerse</h1>
                    </div>
                    <p className="text-muted">
                        {isLogin ? 'Welcome back to the skill universe' : 'Join the global skill network'}
                    </p>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    {!isLogin && (
                        <>
                            <div className="name-row" style={{ display: 'flex', gap: '1rem' }}>
                                <div className="input-group" style={{ flex: 1 }}>
                                    <label>First Name</label>
                                    <input
                                        type="text"
                                        placeholder="John"
                                        value={firstName}
                                        onChange={(e) => setFirstName(e.target.value)}
                                        required
                                    />
                                </div>
                                <div className="input-group" style={{ flex: 1 }}>
                                    <label>Last Name</label>
                                    <input
                                        type="text"
                                        placeholder="Doe"
                                        value={lastName}
                                        onChange={(e) => setLastName(e.target.value)}
                                        required
                                    />
                                </div>
                            </div>
                            <div className="input-group">
                                <label><ShieldCheck size={18} /> Username</label>
                                <input
                                    type="text"
                                    placeholder="johndoe123"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                />
                            </div>
                        </>
                    )}

                    <div className="input-group">
                        <label><Mail size={18} /> Email Address</label>
                        <input
                            type="email"
                            placeholder="name@example.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>

                    <div className="input-group">
                        <label><Lock size={18} /> Password</label>
                        <input
                            type="password"
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    {error && <p className={error.includes('successful') ? 'success-message' : 'error-message'}>{error}</p>}

                    <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
                        {loading ? 'Processing...' : (isLogin ? 'Sign In' : 'Create Account')}
                        <LogIn size={20} />
                    </button>
                </form>

                <div className="login-footer">
                    <p className="text-muted">
                        {isLogin ? "Don't have an account?" : "Already have an account?"}
                        <button className="text-btn" onClick={() => setIsLogin(!isLogin)}>
                            {isLogin ? 'Sign Up' : 'Sign In'}
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;
