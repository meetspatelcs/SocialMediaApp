import { useEffect, useState } from "react";

function useLocalState(defaultValue, key){
   const [value, setValue] = useState(() => {
        const localStorageVal = localStorage.getItem(key);
        // alert(localStorageVal);
        return localStorageVal !== null ? JSON.parse(localStorageVal) : defaultValue;
    });

    useEffect(() => {
        localStorage.setItem(key, JSON.stringify(value));
    }, [key, value]);

    return [value, setValue];
}



export{useLocalState};