import React from 'react';
import { useState } from 'react';
import { useEffect } from 'react';
import { Button, Card, Col, Form, Image, Row } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import ViewPostsComp from '../Cards/ViewPostsComp';
import PersonalInfo from '../Components/PersonalInfo';
import ModalPersonalInfo from '../Modals/ModalPersonalInfo';
import CreatePostModal from '../Modals/CreatePostModal';
import NavBar from '../NavBar/NavBar';
import requestToPath from '../Service/fetchService';
import myImg from '../test/low_poly_purple_abstract_art-wallpaper-1920x1080.jpg';
import { useUser } from '../UserProvider/UserProvider';

const TestMyPosts = () => {
    const user = useUser();
    const {userId} = useParams();
    const[myPosts, setMyPosts] = useState([]);
    const[personalInfo, setPersonalInfo] = useState({});
    const[imgCount, setImageCount] = useState(0); 
    const[show, setShow] = useState(false);

    function handleClose(){
        setShow(false);
    }
    function handleShow(){
        setShow(true);
    }

    useEffect(() => {
        requestToPath(`/api/posts/myPosts`, "GET", user.jwt)
        .then((myPostsData) => {
            // console.log(myPostsData);
            setMyPosts(myPostsData);
        })
    }, [])

    useEffect(() => {
        requestToPath(`/api/UsersInfo/user`, "GET", user.jwt)
        .then((userResponse) => {
            let userData = userResponse;
            console.log(userData);
            if(userData.myBio === null) userData.myBio = "";
            if(userData.email === null) userData.email = "";
            if(userData.phone === null) userData.phone = "";
            setPersonalInfo(userData);
        })
    }, [])

    return (
        <div style={{margin:"0", padding:"0"}}>
            <div className='custom-main'>
                <div> 
                    <view style={{display: "flex", justifyContent:"center",alignItems:"center", width:"100%", height:"15rem", overflow: "hidden"}}>
                    {/* add a field for thumbnail image prop better to create a new entity */}
                    <Image thumbnail src={myImg}  />
                    </view>
                </div>

            <div className='custom-aboutGrid'>
            
                <div className='custom-MyPostWrapper custom-aboutGridEach mt-5 mb-5' >
                    <div className='d-flex justify-content-between'  >
                    <h3 className=' mb-3 '>About</h3>
                    <Button size='sm' onClick={() => {handleShow()}}>More</Button>
                    </div>
                {/* Bio: <br/>
                Name: <br/>
                Age: <br/>
                Location: state, Country <br/>
                Email: <br/>
                Phone: <br/>
                Join Date: <br/> */}
                {personalInfo ? 
                    <div key={personalInfo.id}>
                        <PersonalInfo personalInfo={personalInfo} />
                    </div> : <></>
                }
                </div>

                <div className='custom-MyPostWrapper custom-aboutGridEach mt-5 mb-5' >

                <div className='d-flex justify-content-between'  >
                <h3 className='mb-3'>Media</h3>
                <Button size='sm'>More</Button>
                </div>
                    <div className='custom-postMedia'>
                    {myPosts ? myPosts.filter(eachPost => eachPost.postByte !== null).map((eachMedia) => (
                            <img src={`data:image/jpeg;base64,${eachMedia.postByte.toString()}`} />
                    )) : <></>}
                    </div>
                    <ModalPersonalInfo show={show} emitHandleClose={handleClose} personalInfo={personalInfo} />
                </div>

                <div className='custom-MyPostWrapper custom-aboutGridEach mt-5 mb-5'>
                <h2 className='ms-3 mb-3 px-2' style={{margin:"-1.7em", backgroundColor:"white", width:"min-content", whiteSpace: "nowrap"}}>Follow</h2>
                List of Pages/Games person follows/
                </div>

                <div className='custom-MyPostWrapper custom-aboutGridEach mt-5 mb-5'>
                <h2 className='ms-3 mb-3 px-2' style={{margin:"-1.7em", backgroundColor:"white", width:"min-content", whiteSpace: "nowrap"}}>Friends</h2>
                List of friends
                </div>
            </div>

            <div>
                <div style={{display:"flex", justifyContent:"center", alignItems:"center", height:"200px"}}>
                    <Button className='custom-postBtn' onClick={() => {handleShow()}}>Create Post</Button>
                </div>

                {myPosts ? myPosts.map((eachMyPost) => (
                    <> 
                    <ViewPostsComp eachMyPost={eachMyPost} />
                    </>
                )) : (<>No Posts</>)}
            </div>

            <div style={{textAlign:"center"}}>
                <br/>  Change this to something   <br/>  <br/>  <br/><br/><br/>
            </div>
            </div>
            <NavBar />
        </div>
    );
};

export default TestMyPosts;

 {/* <Card className='custom-postCard'>  
                    <Card.Body>
                        <div className='d-flex justify-content-between'>
                            
                            <div>
                                <Card.Title>{eachMyPost.user.firstname} {eachMyPost.user.lastname}</Card.Title>
                                <Card.Subtitle className="mb-2 text-muted">{eachMyPost.creationDate} change to time collapsed</Card.Subtitle>
                            </div>
                            <div>
                                <Button variant='outline-primary'>Edit</Button> 
                                <Button variant='outline-danger'>Remove</Button>
                            </div>
                        </div>
                        
                        <Card.Text>
                            {eachMyPost.description}
                        </Card.Text>
                    </Card.Body>

                    {eachMyPost.postByte !== null ? <>
                    <Card.Img variant="bottom" src={`data:image/jpeg;base64,${eachMyPost.postByte.toString()}`} style={{maxWidth:"100%", maxHeight:"32rem", width:"auto", height:"auto"}}  />
                    </> : <></>}
                </Card> */}