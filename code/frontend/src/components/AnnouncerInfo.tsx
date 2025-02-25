import * as React from 'react';

export default function AnnouncerInfo({announcer}: {announcer: Announcer}) {
    return (
        <div>
            <h1>Announcer Info</h1>
            <p>Announcer name: {announcer.name}</p>
            <p>Announcer email: {announcer.email}</p>
        </div>
    )
}