
import React, { useEffect, useState } from 'react';
import { Button, Col, Container, Form, Row } from 'react-bootstrap';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { allCountries } from 'country-region-data';

const EditPersonalInfo = () => {
    const user = useUser();
    const {personalInfoId, userId} = useParams();
    const navigate = useNavigate();
    const {state} = useLocation();
 
    const [index, setIndex] = useState(null);
    const [countryNum, setCountryNum] = useState();
    const [stateNum, setStateNum] = useState();
    const [myInfo, setMyInfo] = useState({user: {firstname: "", lastname:""}, 
       country: "", email:"", myBio: "", phone:"", state: ""});

    useEffect(() => {
        if(state != null){
            setMyInfo(state.personalInfo);
            setIndex(state.countryIndex);
        }
    }, [state])

    useEffect(() => {
        if(index != null){setStateNum(allCountries[index][2].findIndex((state) => (state[1] == myInfo.state)));}
    }, [index])

    function updateDetails(prop, value){
        const copyInfo = {...myInfo};
        if(prop === "firstname"){
            console.log("test: ", copyInfo);
            copyInfo.user.firstname = value;
        }
        else if(prop === "lastname"){
            copyInfo.user.lastname = value;
        }
        else{
            copyInfo[prop] = value;
        }
        setMyInfo(copyInfo);
    }
 
    function sendUpdatedInfo(){
        requestToPath(`/api/UsersInfo/${personalInfoId}`, "PUT", user.jwt, myInfo)
            .then((userResponse) => { navigate(`/users/${userId}/myPosts`); })
            .catch((error) => {console.log(error);})
    }

    function displayState(){
        if(countryNum != null && countryNum != index){
            return <Form.Select onChange={(e) =>{updateDetails("state", allCountries[countryNum][2][e.target.value][1]);}} className='custom-formcontrol' size='sm' defaultValue="" style={{padding:"0", textAlign:"initial"}}  > 
                        <option>Select</option>
                        {allCountries[countryNum][2].map((item, key) => <option key={key} value={key}>{item[0]}</option>)}
                    </Form.Select>
        }

        if(countryNum != null){
            return <Form.Select onChange={(e) =>{updateDetails("state", allCountries[countryNum][2][e.target.value][1]);}} className='custom-formcontrol' size='sm' defaultValue="" style={{padding:"0", textAlign:"initial"}}  > 
                        {allCountries[countryNum][2].map((item, key) => <option key={key} value={key}>{item[0]}</option>)}
                    </Form.Select>
        }

        if(index != null && stateNum != null){    
            return <Form.Select onChange={(e) =>{updateDetails("state", allCountries[index][2][e.target.value][1]);}} className='custom-formcontrol' size='sm' defaultValue="" style={{padding:"0", textAlign:"initial"}}  > 
                        <option value="">{allCountries[index][2][stateNum][0]}</option>
                        {allCountries[index][2].map((item, key) => <option key={key} value={key}>{item[0]}</option>)}
                    </Form.Select>
        }
    }

    function displayCountry(){
        if(index != null){
            return <Form.Select onChange={(e) => {updateDetails("country", allCountries[e.target.value][1]); setCountryNum(e.target.value)}} className='custom-formcontrol' size='sm' defaultValue="" style={{padding:"0", textAlign:"initial"}}>
                        <option value="">{allCountries[index][0]}</option> 
                        {allCountries.map((item, key) => <option key={key} value={key}>{item[0]}</option>)}
                    </Form.Select>
        }
    }

    return (
        <Container className='mt-5'>
            <Row>
                <h5>Information</h5>
                <Form.Group as={Col} className="mb-3">
                    <Form.Label>First</Form.Label>
                    <Form.Control placeholder="First" size="sm" style={{ border: "none", backgroundColor: "lightgray"}} 
                        value={myInfo.user.firstname} onChange={(e) => {updateDetails("firstname", e.target.value)}}/>
                </Form.Group>

                <Form.Group as={Col} className="mb-3">
                    <Form.Label>Last</Form.Label>
                    <Form.Control placeholder="Last" size="sm" style={{ border: "none", backgroundColor: "lightgray"}}
                    value={myInfo.user.lastname} onChange={(e) => {updateDetails("lastname",e.target.value)}}/>
                </Form.Group>
            </Row>

            <Row>
                <Form.Group as={Col} className="mb-3">
                    <Form.Label>Bio</Form.Label>
                    <Form.Control as="textarea"  placeholder="Description" size="sm" style={{ border: "none", backgroundColor: "lightgray", resize:"none"}} 
                      value={myInfo.myBio}  onChange={(e) => {updateDetails("myBio",e.target.value)}}/>
                </Form.Group>
            </Row>

            <Row>
                <h5>Region</h5>
                <Form.Group as={Col}  className="mb-3">
                    <Form.Label>State</Form.Label>
                    {displayState()}
                </Form.Group>

                <Form.Group as={Col} className="mb-3">
                    <Form.Label>Country</Form.Label>
                    {displayCountry()}
                </Form.Group>
            </Row>

            <Row>
            <h5>Contact Info</h5>
                <Form.Group as={Col} className="mb-3">
                    <Form.Label>Email</Form.Label>
                    <Form.Control placeholder="No contact email"   size="sm" style={{ border: "none", backgroundColor: "lightgray"}}
                    value={myInfo.email} onChange={(e) => {updateDetails("email",e.target.value)}}/>
                </Form.Group>

                <Form.Group as={Col} className="mb-3">
                    <Form.Label>Phone</Form.Label>
                    <Form.Control placeholder="No contact phone" size="sm" style={{ border: "none", backgroundColor: "lightgray"}}
                    value={myInfo.phone} onChange={(e) => {updateDetails("phone",e.target.value)}}/>
                </Form.Group>
            </Row>       
            <div className='d-flex justify-content-between'>
                <Button onClick={() => {sendUpdatedInfo();}}>Save</Button>
                <Button variant='secondary' onClick={() => {navigate(`/users/${userId}/myPosts`);}}>Cancel</Button>
            </div>
        </Container>
    );
};

export default EditPersonalInfo;