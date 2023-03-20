import React, { useEffect, useState } from 'react';
import { Col, Form, Row } from 'react-bootstrap';
import { allCountries } from 'country-region-data';

const PersonalInfo = (props) => {

    const {personalInfo} = props;
    const countryIndex = allCountries.findIndex((country) => (country[1] == personalInfo.country));
    const [currState, setCurrState] = useState();

    useEffect(() => {
        if(countryIndex != -1){
            setCurrState(allCountries[countryIndex][2].find((state) => (state[1] == personalInfo.state)))
        }
    }, [countryIndex])
    
    function displayCountry(){
        if(countryIndex != -1){
            return <Form.Group as={Col} className="mb-3">
                        <Form.Label>Country</Form.Label>
                        <Form.Control placeholder="Disabled input" disabled value={allCountries[countryIndex][0]} size="sm" style={{ border: "none"}} />
                    </Form.Group>
        }
    }

    function displayState(){
        if(countryIndex != -1 && currState != null){
            return <Form.Group as={Col}  className="mb-3">
                        <Form.Label>State</Form.Label>
                        <Form.Control placeholder="Disabled input" disabled value={currState[0]} size="sm" style={{ border: "none"}} />
                    </Form.Group>
        }
    }
    return (
        <div>
            <Row>
                <h5>Personal Info</h5>
                <Form.Group as={Col} className="mb-3">
                    <Form.Label>Name</Form.Label>
                    <Form.Control placeholder="Disabled input" disabled value={personalInfo.name} size="sm" style={{ border: "none"}} />
                </Form.Group>

                <Form.Group as={Col} lg={1} md={1} sm={1}  className="mb-3">
                    <Form.Label>Age</Form.Label>
                    <Form.Control placeholder="Disabled input" disabled value={personalInfo.age} size="sm" style={{ border: "none"}} />
                </Form.Group>

                {/* <Form.Group as={Col}  className="mb-3">
                    <Form.Label>State</Form.Label>
                    <Form.Control placeholder="Disabled input" disabled value={personalInfo.state} size="sm" style={{ border: "none"}} />
                </Form.Group> */}

                {displayState()}

                {/* <Form.Group as={Col} className="mb-3">
                    <Form.Label>Country</Form.Label>
                    <Form.Control placeholder="Disabled input" disabled value={personalInfo.country} size="sm" style={{ border: "none"}} />
                </Form.Group> */}
                {displayCountry()}
            </Row>

            <Row>
                <Form.Group as={Col} className="mb-3">
                    <Form.Label>Bio</Form.Label>
                    <Form.Control placeholder="User have not set bio" disabled value={personalInfo.myBio} size="sm" style={{ border: "none"}} />
                </Form.Group>
            </Row>

            <Row>
            <h5>Contact Info</h5>
                <Form.Group as={Col} className="mb-3">
                    <Form.Label>Email</Form.Label>
                    <Form.Control placeholder="No contact email" disabled value={personalInfo.email} size="sm" style={{ border: "none"}} />
                </Form.Group>

                <Form.Group as={Col} className="mb-3">
                    <Form.Label>Phone</Form.Label>
                    <Form.Control placeholder="No contact phone" disabled value={personalInfo.phone} size="sm" style={{ border: "none"}} />
                </Form.Group>
            </Row>

        </div>
    );
};

export default PersonalInfo;

