import React, { useState, useEffect } from 'react';
import { Search, MapPin, Star, MessageSquare, Shield } from 'lucide-react';
import axios from 'axios';
import './Matches.css';

const API_BASE = '';

const Matches = ({ onChatStart }) => {
    const [matches, setMatches] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');

    const fetchMatches = async () => {
        setLoading(true);
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`${API_BASE}/api/profiles`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setMatches(response.data);
        } catch (err) {
            console.error('Failed to fetch matches', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMatches();
    }, []);

    const filteredResults = matches.filter(match => {
        if (!searchQuery) return false; // Don't show anything if search is empty
        const query = searchQuery.toLowerCase();
        const nameMatch = match.username?.toLowerCase().includes(query);
        const roleMatch = match.role?.toLowerCase().includes(query);
        const skillMatch = match.skills?.some(s => s.name?.toLowerCase().includes(query)) || false;
        return nameMatch || roleMatch || skillMatch;
    });

    return (
        <div className="matches-container">
            <div className={`search-section ${searchQuery ? 'has-results' : 'hero'}`}>
                <div className="search-box-wrapper glass">
                    <Search size={24} className="text-primary" />
                    <input
                        type="text"
                        placeholder="Search for experts (e.g. React, Docker, DevOps)..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        autoFocus
                    />
                </div>
                {!searchQuery && !loading && (
                    <div className="search-hint">
                        <p>Type a skill to find and connect with experts instantly.</p>
                    </div>
                )}
            </div>

            {loading ? (
                <div className="loading-state">Searching for experts...</div>
            ) : searchQuery && (
                <div className="matches-grid">
                    {filteredResults.map((match) => (
                        <div key={match.id} className="card match-card">
                            <div className="match-header">
                                <div className="match-avatar">
                                    {match.username?.substring(0, 2).toUpperCase()}
                                </div>
                                <div className="match-info">
                                    <h3>{match.username}</h3>
                                    <p className="text-muted"><MapPin size={14} /> Remote</p>
                                </div>
                            </div>

                            <div className="match-body">
                                <p>{match.bio || "Available for collaboration and knowledge sharing."}</p>
                                <div className="skills-tags">
                                    {match.skills && match.skills.length > 0 ? (
                                        match.skills.map(skill => (
                                            <span key={skill.id || skill.name} className="skill-tag">{skill.name}</span>
                                        ))
                                    ) : (
                                        <span className="skill-tag text-muted">No specific skills listed</span>
                                    )}
                                </div>
                            </div>

                            <div className="match-footer">
                                <button className="btn btn-primary btn-full" onClick={() => onChatStart(match)}>
                                    <MessageSquare size={18} /> Connect & Chat
                                </button>
                            </div>
                        </div>
                    ))}

                    {filteredResults.length === 0 && (
                        <div className="empty-state card">
                            <Shield size={48} color="var(--primary)" />
                            <h3>No experts found for "{searchQuery}"</h3>
                            <p className="text-muted">Try a different skill or check your spelling.</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default Matches;
