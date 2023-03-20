import React from 'react';
import { Button, Modal } from 'react-bootstrap';
import PersonalInfo from '../../../Components/MyPosts/About/PersonalInfo';

const ViewPersonalInfo = (props) => {

    const{show, emitHandleClose, personalInfo} = props;
    
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
                    
                    <Button variant='secondary' onClick={() => {emitHandleClose()}}>Close</Button>
                </Modal.Footer>
            </Modal> 
        </div>
    );
};

export default ViewPersonalInfo;