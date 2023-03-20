import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { Button, Form } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../UserProvider/UserProvider';
import { allCountries } from 'country-region-data';

const Register = () => {
    const user = useUser();
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [firstname, setFirstname] = useState("");
    const [lastname, setLastname] = useState("");
    const [dob, setDOB] = useState("");
    const [identification, setIdentification] = useState("");
    const [myState, setMyState] = useState("");
    const [country, setCountry] = useState("");
    const [countryNum, setCountryNum] = useState("");
    const [formError, setFormError] = useState({});
    const [date, setDate] = useState();

    function handleValidation(){
        let err = {};
        if(firstname === ''){
            err.firstname = 'Firstname is required!';
        }
        if(lastname === ''){
            err.lastname = 'Lastname is required!';
        }
        if(username !== ''){
            const temp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            if(!temp.test(username)){
                err.username = 'Invalid Email!'
                return false;
            }
        } else{
            err.username = 'Email is required!';
        }
        if(password === ''){
            err.password = 'Password is required!';
        }
        if(identification === ''){
            err.identification = 'Driver licence or Id is required!'
        }
        setFormError({...err});
        
        const fetchValidation = Object.keys(err).length < 1 
        return fetchValidation;
    }

    useEffect(() => {
        //yyyy-mm-dd
        // let myTest = startDate.toLocaleDateString("zh-Hans-CN");
        let myTest = date;
        
        setDOB(myTest);

    }, [date])

    function createUser(){
        
        const reqBody = {
            firstname: firstname,
            lastname: lastname,
            username: username,
            password: password,
            identification: identification,
            dob: dob,
            myState: myState,
            country: country,
        };

        const isValid = handleValidation();
        
        if(isValid){
            axios.post(`/api/users/register`, reqBody, {
                headers: {
                    "Content-Type": "application/json",
                }
            })
            .then((response) => {
                console.log(response.data);
                 navigate(`/`);
            })
            .catch((error) => {
                console.log(error.response.data);
            });
        }
        else{
            alert('Invalid Form');
        }
    };


    return (
        <div className='box'>
        <Form onSubmit={createUser}>
            <div className='itemsReg pb-3'>
            <h1 className='mt-2 mb-2 text-center'>Sign Up change color</h1>
            <Form.Group  className="mb-1" controlId="First">    
                <Form.Label className="fs-4">Firstname</Form.Label>
                    <Form.Control required className='custom-formcontrol' size="lg" type="text" placeholder="First" onChange={(e) => setFirstname(e.target.value)} />   
                    <span className=''>{formError.firstname}</span>
            </Form.Group>

            <Form.Group className="mb-1" controlId="Last">    
                <Form.Label className="fs-4">Lastname</Form.Label>
                    <Form.Control required className='custom-formcontrol' size="lg" type="text" placeholder="Last" onChange={(e) => setLastname(e.target.value)} />   
                    <span className=''>{formError.lastname}</span>
            </Form.Group>

            <Form.Group className="mb-1" controlId="email">    
                <Form.Label className="fs-4">Username</Form.Label>
                    <Form.Control required className='custom-formcontrol' size="lg" type="email" placeholder="Email" onChange={(e) => setUsername(e.target.value)}  />   
                    <span className=''>{formError.username}</span>
            </Form.Group>

            <Form.Group className="mb-1" controlId="password">
                <Form.Label className="fs-4">Password</Form.Label>
                    <Form.Control required className='custom-formcontrol' size="lg" type="password" placeholder="Password" onChange={(e) => setPassword(e.target.value)} />
                    <span className=''>{formError.password}</span>
            </Form.Group>

            <Form.Group className="mb-1" controlId="identification">    
                <Form.Label className="fs-4">State I.D.:</Form.Label>
                    <Form.Control required className='custom-formcontrol' size="lg" type="text" placeholder="XXXXXXXX" onChange={(e) => setIdentification(e.target.value)} />   
                    <span className=''>{formError.identification}</span>
            </Form.Group>

            <Form.Group className="mb-1" controlId="dob">    
                <Form.Label className="fs-4">Your Birthdate</Form.Label>
                    <Form.Control required className='custom-formcontrol' size="lg" type="date" onChange={(e) => setDate(e.target.value)}  />   
                    <span className=''>{formError.identification}</span>
            </Form.Group>

            {countryNum ? <>
                <Form.Group className="mb-1" controlId="state">    
                    <Form.Label className="fs-4">State</Form.Label>
                    <Form.Select onChange={(e) =>{setMyState(allCountries[countryNum][2][e.target.value][1]);}} className='custom-formcontrol' size='sm' defaultValue="" style={{padding:"0", textAlign:"initial"}}  > 
                        {allCountries[countryNum][2].map((item, key) => <option key={key} value={key}>{item[0]}</option>)}
                    </Form.Select>
                </Form.Group>
            </> :<></>}

            <Form.Group className="mb-3" controlId="country">    
                <Form.Label className="fs-4">Country</Form.Label>
                <Form.Select onChange={(e) =>{setCountry(allCountries[e.target.value][1]); setCountryNum(e.target.value)}} className='custom-formcontrol' size='sm' defaultValue="" style={{padding:"0", textAlign:"initial"}}  > 
                    {allCountries.map((item, key) => <option key={key} value={key}>{item[0]}</option>)}
                </Form.Select>
            </Form.Group>

            <Button  size="lg" className="custom-btn mb-1" variant='secondary' type="submit">Create</Button>
            <Button  size="lg" className="custom-btn"  onClick={() => navigate("/")}>Already have an account</Button>
            </div>
        </Form>
        </div>
    );
};

export default Register;