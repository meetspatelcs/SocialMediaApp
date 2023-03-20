import React, { useEffect, useState } from 'react';
import { Button, Card } from 'react-bootstrap';
import ViewDashPostsPage from '../Cards/Dashboard/ViewDashPostsPage';
import ViewDashPosts from '../Cards/ViewDashPosts';
import CreatePostModal from '../Modals/CreatePostModal';
import NavBar from '../NavBar/NavBar';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';


const Dashboard = () => {
    const user = useUser();
    const[show, setShow] = useState(false);
    const[testPost, setTestPosts] = useState([]);
    const[myInfo, setMyInfo] = useState({});

    useEffect(() =>{
        requestToPath("/api/users/myDetails", "GET", user.jwt)
            .then((myData) => { setMyInfo(myData); })
            .catch((error) => { console.log(error); })
    },[]);

    useEffect(() => {
        requestToPath(`/api/posts/dashboard`, "GET", user.jwt)
            .then((postResponse) => { setTestPosts(postResponse);})
            .catch((error) => { console.log(error); })
    }, [])

    function handleShow(){setShow(true);}
    function handleClose(){setShow(false);}

    useEffect(() => { testPost.sort() }, [testPost])

    function displayPosts(){
        if(testPost != ''){
            return testPost.sort((a,b) => b.id > a.id ? 1 : -1)
            .map((eachPost, index) => {
                if(eachPost.user){ return <div key={index} className='p-1'><ViewDashPosts eachPost={eachPost} loggedUser={myInfo.id}/></div> }
                else{ return <div key={index} className='p-1'><ViewDashPostsPage currPost={eachPost} loggedUser={myInfo.id} /></div> }
            })
        }
    }

    return (
        <div style={{margin:"0", padding:"0"}}>
            <div className='custom-main'>
                <div className='d-flex justify-content-center align-items-center' style={{height:"200px"}}>
                    <Button className='custom-postBtn' onClick={() => {handleShow()}}>Create Post</Button>
                </div>
                    {displayPosts()}
                <div className='text-center mb-5'> No Posts to see </div>
            </div>

            <CreatePostModal show={show} emitHandleClose={handleClose} />
            <NavBar/>
        </div>
    );
};

export default Dashboard;