import * as React from 'react';
import './App.css';

import logo from './logo.svg';

interface CourseSection {
    courseID: string;
    title: string;
    scheduleNum: string;
    units: string;
    seats: string;
    days: Array<string>;
    times: Array<string>;
    locations: Array<string>;
    instructors: Array<string>;
    parentCourse: CourseSection;
}

interface Schedule {
    coursesInSchedule: Array<CourseSection>;
}

interface RankedSchedules {
    id: number;
    resultCode: string;
    completionTime: string;
    numValidSchedules: number;
    numInvalidSchedules: number
    numPerformedPermutations: number
    numTheoreticalPermutations: number;
    userCourseSelection: Array<string>;
    topRankedValidSchedules: Array<Schedule>;
    coursesWithAllSectionsFull: Array<string>;
    coursesWithNoSectionsOffered: Array<string>;
}

interface AppProps {
}

interface AppState {
    schedules: RankedSchedules;
    isLoading: boolean;
}

class App extends React.Component<AppProps, AppState> {

    constructor(props: AppProps) {
        super(props);

    }

    componentDidMount() {
        this.setState({isLoading: true});

        fetch('http://localhost:8080/v1/test')
            .then(response => response.json())
            //.then(data => this.setState({schedules: data, isLoading: false}));
    }

    render() {
          const {schedules, isLoading} = this.state;

          if(isLoading) {
              return <p>Loading...</p>;
          }

        return (
          <div className="App">
            <header className="App-header">
              <img src={logo} className="App-logo" alt="logo" />
              <h1 className="App-title">Welcome to React</h1>
            </header>
              <div>

              </div>
          </div>
        );
      }


}

export default App;
