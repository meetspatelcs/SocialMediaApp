import React from 'react';
import { Button, Modal } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import PersonalInfo from '../Components/MyPosts/About/PersonalInfo';
import { useUser } from '../UserProvider/UserProvider';
import { allCountries } from 'country-region-data';

const ModalPersonalInfo = (props) => {
    const user = useUser();
    const {userId} = useParams();
    const{show, emitHandleClose, personalInfo} = props;
    const navigate = useNavigate();
    const countryIndex = allCountries.findIndex((country) => (country[1] == personalInfo.country));
    
   
    return (
        <div>
            <Modal size='lg' fullscreen show={show} onHide={emitHandleClose}>
                <Modal.Header className='custom-modalHeader'>
                    <Modal.Title ><h1>About</h1> </Modal.Title>
                </Modal.Header>
                <Modal.Body> 
                    
                    <PersonalInfo personalInfo={personalInfo} />
                </Modal.Body>
                <Modal.Footer>
                    {/* go to a different page */}
                    <Button onClick={()=> {emitHandleClose(); navigate(`/users/${userId}/myPosts/myInfo/${personalInfo.id}/Edit`, {state:{personalInfo, countryIndex}})}}>Edit</Button>
                    <Button variant='secondary' onClick={() => {emitHandleClose()}}>Close</Button>
                </Modal.Footer>
            </Modal> 
        </div>
    );
};

export default ModalPersonalInfo;