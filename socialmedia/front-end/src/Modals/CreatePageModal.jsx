import React, { useState } from 'react';
import { Button, Form, Modal } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';

const CreatePageModal = (props) => {
    const user = useUser();
    const {userId} = useParams();
    const navigate = useNavigate();
    const {emitHandleClose, show} = props;
    
    const [branch, setBranch] = useState(null);
    const [mainBranch, setMainBranch] = useState(null);
    const [desc, setDesc] = useState(null);
    const [email, setEmail] = useState(null);
    const [phone, setPhone] = useState(null);

    function createPageRequest(){
        const reqBody = {
            branch: branch,
            mainBranch: mainBranch,
            desc: desc,
            email: email,
            phone: phone,
        }

        requestToPath(`/api/pages`, "POST", user.jwt, reqBody)
            .then((pageResponse) => { navigate(`/users/${userId}/pages/page/${pageResponse.id}`); })
            .catch((error) => {console.log(error);})
    }

    return (
        <div>
            <Modal fullscreen size='lg' show={show} onHide={emitHandleClose}>
                <Modal.Header className='custom-modalHeader'>
                    <Modal.Title ><h1>Create Page</h1> </Modal.Title>
                </Modal.Header>
                <Modal.Body> 
                    <Form onSubmit={createPageRequest}>
                        <Form.Group className="mb-1">    
                            <Form.Label className="fs-4">Corporation Name</Form.Label>
                            <Form.Control required className='custom-formcontrol' size="lg" type="text" placeholder="Name" onChange={(e) => {setBranch(e.target.value)}} />   
                        </Form.Group>

                        <Form.Group className="mb-1">    
                            <Form.Label className="fs-4">Main Corporation</Form.Label>
                            <Form.Control required className='custom-formcontrol' size="lg" type="text" placeholder="Parent Company" onChange={(e) => {setMainBranch(e.target.value)}}  />   
                        </Form.Group>

                        <Form.Group className="mb-1">    
                            <Form.Label className="fs-4">Bio</Form.Label>
                            <Form.Control required className='custom-formcontrol' size="lg" as="textarea" placeholder="Bio" onChange={(e) => {setDesc(e.target.value)}} />   
                        </Form.Group>

                        <Form.Group className="mb-1">    
                            <Form.Label className="fs-4">Email</Form.Label>
                            <Form.Control required className='custom-formcontrol' size="lg" type="text" placeholder="myEmail@example.com" onChange={(e) => {setEmail(e.target.value)}} />   
                        </Form.Group>

                        <Form.Group className="mb-1">    
                            <Form.Label className="fs-4">Phone</Form.Label>
                            <Form.Control required className='custom-formcontrol' size="lg" type="text" placeholder="1234567890" onChange={(e) => {setPhone(e.target.value)}} />   
                        </Form.Group>
                        <Button type='submit' style={{width:"60%", marginLeft: "20%", marginRight:"20%"}}> Create </Button>
                        <Button className='mt-3' variant="secondary" style={{width:"60%", marginLeft: "20%", marginRight:"20%"}}  onClick={emitHandleClose}>Cancel</Button>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                </Modal.Footer>
            </Modal> 
        </div>
    );
};

export default CreatePageModal;