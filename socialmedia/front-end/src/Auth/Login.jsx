import React, { useEffect, useState } from 'react';
import { Button, Form, } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../UserProvider/UserProvider';

const Login = () => {
    const user = useUser();
    
    const[username, setUsername] = useState("");
    const[password, setPassword] = useState("");

    const navigate = useNavigate();

    useEffect(()=> {
        if(user.jwt)
            navigate(`/dashboard`);
        
    },[user]);

    function sendLoginRequest(){
        const reqBody = {
            username: username,
            password: password,
        };

        fetch("api/auth/login", {
            headers: {
                "Content-Type": "application/json",
            },
            method: "post",
            body: JSON.stringify(reqBody),
        }).then((response)=>{
            if(response.status === 200)
                return Promise.all([response.json(), response.headers]);
            else
                return Promise.reject("Invalid login attempt!");
        }).then(([body, headers]) => {
            
            user.setJwt(headers.get("authorization"));
            
        }).catch((message) => {
            alert(message);
        });
    };

    return (
        
        <div className='box'>
            <div className='items'>
            <h1 className='mt-5 mb-5 text-center'>Log In</h1>         
            <Form.Group className="mb-4" controlId="email">    
                <Form.Label className="fs-4">Username</Form.Label>
                    <Form.Control className='custom-formcontrol' size="lg" type="Email" placeholder="Email" onChange={(e) => setUsername(e.target.value)} />   
            </Form.Group>

            <Form.Group className="mb-3" controlId="password">
                <Form.Label className="fs-4">Password</Form.Label>
                    <Form.Control className='custom-formcontrol'  size="lg" type="password" placeholder="Password" onChange={(e) => setPassword(e.target.value)} />
            </Form.Group>

            <Button  size="lg" className="custom-btn"  onClick={() => sendLoginRequest()}>Login</Button>
            <Button  size="lg" className="custom-btn" variant='secondary' onClick={()=>navigate("/register")}>Register</Button>
            </div>
        </div>
    );
};

export default Login;