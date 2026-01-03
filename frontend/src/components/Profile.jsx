import React, { useState, useEffect } from 'react';
import { User, Mail, Briefcase, Award, Save, Camera, Plus, X } from 'lucide-react';
import axios from 'axios';
import './Profile.css';

const Profile = ({ user }) => {
    const [profile, setProfile] = useState({
        username: user?.username || '',
        email: user?.email || '',
        bio: '',
        role: 'Learner',
        skills: [], // Array of objects {id, name} or just strings depending on backend
        photoUrl: ''
    });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [newSkill, setNewSkill] = useState('');
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            const token = localStorage.getItem('token');
            // Try to get profile by email
            const response = await axios.get(`/api/profiles/email/${user.email}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.data) {
                setProfile(response.data);
            }
        } catch (err) {
            console.log('Profile not found, ready to create one.');
            // Keep default state initialized from user object
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        setMessage('');

        try {
            const token = localStorage.getItem('token');
            let response;

            // If profile has ID, update. Else create.
            if (profile.id) {
                response = await axios.put(`/api/profiles/${profile.id}`, profile, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            } else {
                response = await axios.post('/api/profiles', profile, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            }

            setProfile(response.data);
            setMessage('Profile saved successfully!');
        } catch (err) {
            console.error(err);
            setMessage('Failed to save profile.');
        } finally {
            setSaving(false);
        }
    };

    const handleAddSkill = async () => {
        if (!newSkill.trim()) return;

        try {
            setSaving(true);
            // 1. Fetch all skills to see if it exists
            const skillsResponse = await axios.get('/api/skills');
            const allSkills = skillsResponse.data || [];

            let targetSkill = allSkills.find(s => s.name.toLowerCase() === newSkill.trim().toLowerCase());

            // 2. If not found, create it
            if (!targetSkill) {
                const createResponse = await axios.post('/api/skills', {
                    name: newSkill.trim(),
                    description: `${newSkill} skill`
                });
                targetSkill = createResponse.data;
            }

            // 3. Add to profile using targetSkill.id
            if (targetSkill && targetSkill.id && profile.id) {
                const token = localStorage.getItem('token');
                const addResponse = await axios.post(`/api/profiles/${profile.id}/skills`,
                    { id: targetSkill.id },
                    { headers: { Authorization: `Bearer ${token}` } }
                );

                // 4. Update local state
                setProfile(addResponse.data);
                setNewSkill('');
                setMessage(`Skill '${targetSkill.name}' added!`);
            }

        } catch (err) {
            console.error("Failed to add skill", err);
            setMessage("Error adding skill. Please try again.");
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <div className="loading">Loading profile...</div>;

    return (
        <div className="profile-container">
            <div className="profile-header card glass">
                <div className="profile-avatar-wrapper">
                    <div className="profile-avatar-large">
                        {profile.username?.substring(0, 2).toUpperCase()}
                    </div>
                    <button className="edit-avatar-btn">
                        <Camera size={18} />
                    </button>
                </div>
                <div className="profile-title">
                    <h2>{profile.username}</h2>
                    <p className="text-muted">{profile.email}</p>
                </div>
            </div>

            <form onSubmit={handleSubmit} className="profile-form card">
                <div className="form-group">
                    <label><User size={18} /> Display Name</label>
                    <input
                        type="text"
                        value={profile.username}
                        onChange={(e) => setProfile({ ...profile, username: e.target.value })}
                    />
                </div>

                <div className="form-group">
                    <label><Briefcase size={18} /> Bio</label>
                    <textarea
                        rows="4"
                        placeholder="Tell us about your skills and goals..."
                        value={profile.bio}
                        onChange={(e) => setProfile({ ...profile, bio: e.target.value })}
                    />
                </div>

                <div className="form-group">
                    <label><Award size={18} /> Skills</label>
                    <div className="skills-input-group">
                        <input
                            type="text"
                            placeholder="Add a new skill (e.g. React)..."
                            value={newSkill}
                            onChange={(e) => setNewSkill(e.target.value)}
                            onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), handleAddSkill())}
                        />
                        <button type="button" className="btn btn-secondary" onClick={handleAddSkill}>
                            <Plus size={18} />
                        </button>
                    </div>
                    <div className="skills-list">
                        {profile.skills && profile.skills.map((skill, idx) => (
                            <span key={idx} className="skill-chip">
                                {skill.name}
                                {/* Delete skill not implemented in backend easily, so hiding remove for now */}
                            </span>
                        ))}
                    </div>
                </div>

                <div className="form-actions">
                    <span className={message.includes('success') ? 'text-success' : 'text-error'}>
                        {message}
                    </span>
                    <button type="submit" className="btn btn-primary" disabled={saving}>
                        {saving ? 'Saving...' : 'Save Profile'} <Save size={18} />
                    </button>
                </div>
            </form>
        </div>
    );
};

export default Profile;
