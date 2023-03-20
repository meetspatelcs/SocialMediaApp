import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import CreatedCards from '../Cards/CreatedCards';
import CreatePageModal from '../Modals/CreatePageModal';
import NavBar from '../NavBar/NavBar';
import PageNavbar from '../NavBar/PageNavbar';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';

const MyPages = () => {
    const user = useUser();
    const {userId} = useParams();
    const navigate = useNavigate();
    const [pagesInfo, setPagesInfo] = useState([]); 
    const [show, setShow] = useState(false);

    function handleShow(){ setShow(true); }
    function handleClose(){ setShow(false); }

    useEffect(() => {
        requestToPath(`/api/pages/userCreated`, "GET", user.jwt)
            .then((pagesResponse) => { setPagesInfo(pagesResponse); })
            .catch((error) => {console.log(error);})
    },[])

    function displayPages(){
        if(pagesInfo != ''){
            return pagesInfo.sort((a,b) => b.id > a.id ? 1 : -1)
                .map((eachPage) => {
                    return <CreatedCards key={eachPage.id} pageId={eachPage.id} 
                    pageName={eachPage.compName} 
                    pageDesc={eachPage.compDesc} />
                })
        }
        return <div>No pages created yet!</div> 
    }

    return (
        <div>
            <div className='custom-main'>
                <PageNavbar />
                <div style={{display:"flex", justifyContent:"center", alignItems:"center", height:"200px"}}>
                    <Button className='custom-postBtn' onClick={handleShow}>Create Page</Button>
                </div>

                <div style={{marginBottom: "10vh"}}>
                    {displayPages()}
                    <div className='d-flex justify-content-center'>
                        No More
                    </div>
                </div>
            </div>
            <NavBar />
            <CreatePageModal show={show} emitHandleClose={handleClose} />
        </div>
    );
};

export default MyPages;