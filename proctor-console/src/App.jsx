import React, { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
    const [audits, setAudits] = useState([]);

    useEffect(() => {
        const fetchAudits = async () => {
            try {
                const response = await axios.get('/api/audit');
                setAudits(response.data);
            } catch (error) {
                console.error('Error fetching audits:', error);
            }
        };

        fetchAudits();
        const interval = setInterval(fetchAudits, 5000); // Poll every 5s
        return () => clearInterval(interval);
    }, []);

    return (
        <div className="container">
            <div className="header">
                <h1>Proctoring Dashboard</h1>
            </div>

            <div className="card">
                <h2>Live Suspicion Feed</h2>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ textAlign: 'left' }}>
                            <th>Time</th>
                            <th>Session ID</th>
                            <th>Event Type</th>
                            <th>Score</th>
                            <th>Reason</th>
                        </tr>
                    </thead>
                    <tbody>
                        {audits.map((audit) => (
                            <tr key={audit.id} style={{ borderBottom: '1px solid #eee' }}>
                                <td>{new Date(audit.createdAt).toLocaleTimeString()}</td>
                                <td>{audit.sessionId}</td>
                                <td>{audit.eventType}</td>
                                <td className={
                                    audit.score > 50 ? 'score-high' :
                                        audit.score > 20 ? 'score-medium' : 'score-low'
                                }>
                                    {audit.score ? audit.score.toFixed(1) : '-'}
                                </td>
                                <td>{audit.reason || '-'}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default App;
