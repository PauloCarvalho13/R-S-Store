import { useState } from 'react';

export default function useToggle(): {isToggled: boolean ,toggle: () => void} {
    const [isToggled, setIsToggled] = useState(false);
    const toggle = () => setIsToggled(!isToggled);
    return {isToggled, toggle};
}