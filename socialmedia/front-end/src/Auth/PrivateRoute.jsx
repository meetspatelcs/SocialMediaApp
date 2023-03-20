import React, { useState } from 'react';
import { Spinner } from 'react-bootstrap';
import { Navigate } from 'react-router-dom';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';

const PrivateRoute = (props) => {
    const user = useUser();
  
    const [isLoading, setIsLoading] = useState(true);
    const [isValid, setIsValid] = useState(null);
    const {children} = props;
    
    if(user){
        requestToPath(`/api/auth/validate?token=${user.jwt}`, "GET", user.jwt)
        .then((isValid) => {
            setIsValid(isValid);
            setIsLoading(false);
        });
    }
    else{
        return <Navigate to ="/" />;
    }

    return isLoading ? <div><Spinner animation='grow' variant='dark'/>Loading...</div> : (isValid === true) ? children : <Navigate to ="/" />; 
};

export default PrivateRoute;