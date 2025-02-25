import * as React from 'react';

export default function CreateUserForm() {
    return (
        <div>
            <h1>Create User</h1>
            <form>
                <input type="text" name="email" placeholder="Email" />
                <input type="password" name="password" placeholder="Password" />
                <button type="submit">Create User</button>
            </form>
        </div>
    )
}