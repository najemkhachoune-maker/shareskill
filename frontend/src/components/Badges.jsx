import React, { useState, useEffect } from 'react';
import { Award, Star } from 'lucide-react';
import axios from 'axios';
import './Badges.css';

const Badges = ({ user }) => {
    const [badges, setBadges] = useState([]);
    const [profileId, setProfileId] = useState(null);

    useEffect(() => {
        fetchProfileAndBadges();
    }, [user]);

    const fetchProfileAndBadges = async () => {
        try {
            const token = localStorage.getItem('token');
            // Fetch from Reputation Service
            // Endpoint: /api/reputation/users/{userId}/badges
            const response = await axios.get(`/api/reputation/users/${user.id}/badges`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            // response.data is an array of badges
            setBadges(response.data || []);
            setProfileId(user.id); // Just to keep state consistent if needed
        } catch (err) {
            console.error("Failed to load badges from Reputation Service", err);
        }
    };

    const simulateBadge = async () => {
        if (!user.id) return;
        try {
            const token = localStorage.getItem('token');
            // Call the Simulation Endpoint in Reputation Service
            // This generates 4 reviews to satisfy the "More than 3 people" rule
            await axios.post(`/api/reputation/simulation/competence/${user.id}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });

            alert("Simulation started: 4 Reviews from different users have been generated. The 'Competent Professional' badge should now be awarded.");

            // Refresh badges
            fetchProfileAndBadges();
        } catch (err) {
            console.error("Failed to simulate competence", err);
            alert("Failed to run simulation. Ensure Reputation Service is running.");
        }
    };

    return (
        <div className="badges-container">
            <header className="badges-header">
                <h2><Award className="icon" /> My Achievements</h2>
            </header>

            <div className="badges-grid">
                {badges.length > 0 ? badges.map((badge, idx) => (
                    <div key={idx} className="card badge-card">
                        <div className="badge-icon">
                            <Star size={32} color="gold" fill="gold" />
                        </div>
                        <h3>{badge.name}</h3>
                        <p>{badge.description}</p>
                    </div>
                )) : (
                    <div className="empty-badges">
                        <Award size={48} className="text-muted" />
                        <p>No badges earned yet. Keep learning!</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Badges;
